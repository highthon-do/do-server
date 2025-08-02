package com.highthon.challenge.domain.badge.repository

import com.highthon.challenge.domain.badge.entity.BadgeEntity
import com.highthon.challenge.domain.badge.enums.BadgeType
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface BadgeRepository : JpaRepository<BadgeEntity, Long> {
    fun existsByUserIdAndType(
        userId: Long,
        type: BadgeType,
    ): Boolean

    fun findAllByUserId(userId: Long): List<BadgeEntity>
}
