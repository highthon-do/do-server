package com.highthon.challenge.domain.badge.service

import com.highthon.challenge.domain.badge.dto.response.BadgeProgressResponse
import com.highthon.challenge.domain.badge.dto.response.BadgeResponse
import com.highthon.challenge.domain.badge.entity.BadgeEntity
import com.highthon.challenge.domain.badge.enums.BadgeType
import com.highthon.challenge.domain.badge.repository.BadgeRepository
import com.highthon.challenge.domain.mission.repository.MissionRepository
import com.highthon.challenge.domain.user.repository.UserRepository
import com.highthon.challenge.global.security.holder.AuthenticationHolder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate

@Service
class BadgeService(
    private val userRepository: UserRepository,
    private val missionRepository: MissionRepository,
    private val badgeRepository: BadgeRepository,
    private val authenticationHolder: AuthenticationHolder,
) {
    fun getBadges(): List<BadgeResponse> {
        val userId = authenticationHolder.getCurrentUserId()

        return badgeRepository.findAllByUserId(userId).map { badge ->
            BadgeResponse(
                title = badge.type.title,
                description = badge.type.description,
                grantedAt = badge.grantedAt,
            )
        }
    }

    @Transactional
    fun evaluateAndGrantBadges(userId: Long) {
        val user = userRepository.findById(userId).orElseThrow()

        val ownedBadges = badgeRepository.findAllByUserId(userId).map { it.type }.toSet()
        val missionCount = missionRepository.countByWriterId(userId)
        val missionDates = missionRepository.findMissionDatesByWriterId(userId)

        val streakDays = calculateStreakDays(missionDates)

        val toGrant = BadgeType.entries
            .filterNot { it in ownedBadges }
            .filter { badge ->
                when (val condition = badge.condition) {
                    is BadgeType.Condition.MissionCount -> missionCount >= condition.count
                    is BadgeType.Condition.Streak -> streakDays >= condition.days
                    is BadgeType.Condition.TopPercent -> isUserInTopPercent(userId, condition.percent.toDouble())
                }
            }

        toGrant.forEach { badgeType ->
            badgeRepository.save(
                BadgeEntity(
                    user = user,
                    type = badgeType,
                    grantedAt = LocalDate.now().atStartOfDay()
                )
            )
        }
    }

    @Transactional(readOnly = true)
    fun getBadgeProgress(): List<BadgeProgressResponse> {
        val userId = authenticationHolder.getCurrentUserId()
        val ownedBadges = badgeRepository.findAllByUserId(userId).associateBy { it.type }
        val missionCount = missionRepository.countByWriterId(userId)
        val missionDates = missionRepository.findMissionDatesByWriterId(userId)
        val streakDays = calculateStreakDays(missionDates)

        return BadgeType.entries.map { badge ->
            val achieved = badge in ownedBadges
            val grantedAt = ownedBadges[badge]?.grantedAt

            val progress = when (val condition = badge.condition) {
                is BadgeType.Condition.MissionCount -> {
                    ((missionCount.toDouble() / condition.count) * 100).coerceAtMost(100.0).toInt()
                }

                is BadgeType.Condition.Streak -> {
                    ((streakDays.toDouble() / condition.days) * 100).coerceAtMost(100.0).toInt()
                }

                is BadgeType.Condition.TopPercent -> {
                    val rank = getUserRankPercent(userId)
                    if (rank <= condition.percent) 100 else 0
                }
            }

            BadgeProgressResponse(
                title = badge.title,
                description = badge.description,
                progress = progress,
                achieved = achieved,
                grantedAt = grantedAt,
            )
        }
    }

    private fun calculateStreakDays(dates: List<LocalDate>): Int {
        if (dates.isEmpty()) return 0
        val sorted = dates.distinct().sortedDescending()

        var streak = 1
        var current = sorted.first()

        for (i in 1 until sorted.size) {
            if (sorted[i] == current.minusDays(1)) {
                streak++
                current = sorted[i]
            } else {
                break
            }
        }

        return streak
    }

    private fun getUserRankPercent(userId: Long): Double {
        val userMissionCounts: List<Pair<Long, Int>> = missionRepository.findAllUserMissionCounts()
            .map { (id, count) -> id to (count as Number).toInt() }

        val totalUsers = userMissionCounts.size

        val sorted = userMissionCounts.sortedByDescending { it.second }
        val rank = sorted.indexOfFirst { it.first == userId } + 1

        return (rank.toDouble() / totalUsers) * 100
    }

    fun isUserInTopPercent(userId: Long, percent: Double): Boolean {
        val missionCounts = missionRepository.findAllUserMissionCounts()
        if (missionCounts.isEmpty()) return false

        val sorted = missionCounts.sortedByDescending { it.second }
        val rank = sorted.indexOfFirst { it.first == userId } + 1
        if (rank <= 0) return false

        val threshold = (missionCounts.size * (percent / 100.0)).toInt().coerceAtLeast(1)
        return rank <= threshold
    }
}
