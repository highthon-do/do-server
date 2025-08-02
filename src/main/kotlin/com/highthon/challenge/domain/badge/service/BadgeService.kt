package com.highthon.challenge.domain.badge.service

import com.highthon.challenge.domain.badge.dto.response.BadgeProgressResponse
import com.highthon.challenge.domain.badge.dto.response.BadgeResponse
import com.highthon.challenge.domain.badge.entity.BadgeEntity
import com.highthon.challenge.domain.badge.enums.BadgeType
import com.highthon.challenge.domain.badge.repository.BadgeRepository
import com.highthon.challenge.domain.mission.repository.MissionRepository
import com.highthon.challenge.domain.user.repository.UserRepository
import com.highthon.challenge.global.security.holder.AuthenticationHolder
import org.slf4j.LoggerFactory
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
    private val log = LoggerFactory.getLogger(this::class.java)

    /**
     * 사용자가 획득한 모든 뱃지 목록을 조회합니다.
     * @return 사용자의 뱃지 목록
     */
    @Transactional(readOnly = true)
    fun getBadges(): List<BadgeResponse> {
        val userId = authenticationHolder.getCurrentUserId()
        log.info("Fetching badges for user ID: {}", userId)
        return badgeRepository.findAllByUserId(userId).map(::toBadgeResponse).also { badges ->
            log.info("Found {} badges for user ID: {}", badges.size, userId)
        }
    }

    /**
     * 사용자의 뱃지 획득 조건을 평가하고 새로운 뱃지를 부여합니다.
     * @param userId 평가할 사용자의 ID
     */
    @Transactional
    fun evaluateAndGrantBadges(userId: Long) {
        log.info("Evaluating badges for user ID: {}", userId)
        val user = userRepository.findById(userId).orElseThrow()
        val badgeMetrics = calculateBadgeMetrics(userId)

        val toGrant = BadgeType.entries
            .filterNot { it in badgeMetrics.ownedBadges }
            .filter { badge ->
                isBadgeConditionMet(badge, badgeMetrics)
            }

        log.info("Found {} new badges to grant for user ID: {}", toGrant.size, userId)

        toGrant.forEach { badgeType ->
            log.debug("Granting badge '{}' to user ID: {}", badgeType.title, userId)
            badgeRepository.save(
                BadgeEntity(
                    user = user,
                    type = badgeType,
                    grantedAt = LocalDate.now().atStartOfDay()
                )
            )
        }
    }

    /**
     * 모든 뱃지에 대한 사용자의 진행 상황을 조회합니다.
     * @return 뱃지별 진행 상황 목록
     */
    @Transactional(readOnly = true)
    fun getBadgeProgress(): List<BadgeProgressResponse> {
        val userId = authenticationHolder.getCurrentUserId()
        log.info("Calculating badge progress for user ID: {}", userId)

        val metrics = calculateBadgeMetrics(userId)
        return BadgeType.entries.map { badge ->
            val achieved = badge in metrics.ownedBadges
            val grantedAt = metrics.ownedBadgesMap[badge]?.grantedAt
            val progress = calculateBadgeProgress(badge, metrics)

            log.debug(
                "Badge '{}' progress for user ID {}: {}% (Achieved: {})",
                badge.title, userId, progress, achieved
            )

            BadgeProgressResponse(
                title = badge.title,
                description = badge.description,
                progress = progress,
                achieved = achieved,
                grantedAt = grantedAt,
            )
        }
    }

    /**
     * 특정 뱃지의 진행도를 계산합니다.
     */
    private fun calculateBadgeProgress(badge: BadgeType, metrics: BadgeMetrics): Int {
        return when (val condition = badge.condition) {
            is BadgeType.Condition.MissionCount -> {
                ((metrics.missionCount.toDouble() / condition.count) * 100).coerceAtMost(100.0).toInt()
            }

            is BadgeType.Condition.Streak -> {
                ((metrics.streakDays.toDouble() / condition.days) * 100).coerceAtMost(100.0).toInt()
            }

            is BadgeType.Condition.TopPercent -> {
                if (metrics.userRankPercent <= condition.percent) 100 else 0
            }
        }.also { progress ->
            log.trace("Calculated progress for badge '{}': {}%", badge.title, progress)
        }
    }

    /**
     * 뱃지 획득 조건이 충족되었는지 확인합니다.
     */
    private fun isBadgeConditionMet(badge: BadgeType, metrics: BadgeMetrics): Boolean {
        val result = when (val condition = badge.condition) {
            is BadgeType.Condition.MissionCount -> metrics.missionCount >= condition.count
            is BadgeType.Condition.Streak -> metrics.streakDays >= condition.days
            is BadgeType.Condition.TopPercent -> isUserInTopPercent(metrics.userId, condition.percent.toDouble())
        }
        log.debug("Badge condition check '{}' for user {}: {}", badge.title, metrics.userId, result)
        return result
    }

    /**
     * 뱃지 평가에 필요한 모든 메트릭을 계산합니다.
     */
    private data class BadgeMetrics(
        val userId: Long,
        val missionCount: Long,
        val streakDays: Int,
        val userRankPercent: Double,
        val ownedBadges: Set<BadgeType>,
        val ownedBadgesMap: Map<BadgeType, BadgeEntity>
    )

    private fun calculateBadgeMetrics(userId: Long): BadgeMetrics {
        log.debug("Calculating badge metrics for user ID: {}", userId)
        val ownedBadgesEntities = badgeRepository.findAllByUserId(userId)
        val missionCount = missionRepository.countByWriterId(userId)

        val missionDates = missionRepository.findMissionDatesByWriterId(userId)
            .map { it.toLocalDate() }

        val streakDays = calculateStreakDays(missionDates)
        val userRankPercent = getUserRankPercent(userId)

        return BadgeMetrics(
            userId = userId,
            missionCount = missionCount,
            streakDays = streakDays,
            userRankPercent = userRankPercent,
            ownedBadges = ownedBadgesEntities.map { it.type }.toSet(),
            ownedBadgesMap = ownedBadgesEntities.associateBy { it.type }
        ).also {
            log.debug(
                "Badge metrics for user {}: missions={}, streak={}, rank={}%",
                userId, missionCount, streakDays, userRankPercent
            )
        }
    }

    private fun toBadgeResponse(badge: BadgeEntity) = BadgeResponse(
        title = badge.type.title,
        description = badge.type.description,
        grantedAt = badge.grantedAt,
    )

    /**
     * 연속 미션 수행 일수를 계산합니다.
     */
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

        log.debug("Calculated streak days: {} from {} dates", streak, dates.size)
        return streak
    }

    /**
     * 사용자의 미션 수행 순위 백분율을 계산합니다.
     */
    private fun getUserRankPercent(userId: Long): Double {
        val userMissionCounts = missionRepository.findAllUserMissionCounts()
            .map { (id, count) -> id to count.toInt() }

        val totalUsers = userMissionCounts.size
        val sorted = userMissionCounts.sortedByDescending { it.second }
        val rank = sorted.indexOfFirst { it.first == userId } + 1

        return (rank.toDouble() / totalUsers * 100).also {
            log.debug("User {} rank: {} out of {} users ({}%)", userId, rank, totalUsers, it)
        }
    }

    /**
     * 사용자가 상위 일정 퍼센트 안에 들어가는지 확인합니다.
     */
    fun isUserInTopPercent(userId: Long, percent: Double): Boolean {
        val missionCounts = missionRepository.findAllUserMissionCounts()
        if (missionCounts.isEmpty()) {
            log.debug("No mission counts found for top percent calculation")
            return false
        }

        val sorted = missionCounts.sortedByDescending { it.second }
        val rank = sorted.indexOfFirst { it.first == userId } + 1
        if (rank <= 0) {
            log.debug("User {} not found in mission counts", userId)
            return false
        }

        val threshold = (missionCounts.size * (percent / 100.0)).toInt().coerceAtLeast(1)
        return (rank <= threshold).also {
            log.debug(
                "User {} rank {} is{} in top {}% (threshold: {})",
                userId, rank, if (it) "" else " not", percent, threshold
            )
        }
    }
}
