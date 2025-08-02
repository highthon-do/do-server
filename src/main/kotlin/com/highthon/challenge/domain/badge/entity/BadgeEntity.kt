package com.highthon.challenge.domain.badge.entity

import com.highthon.challenge.domain.badge.enums.BadgeType
import com.highthon.challenge.domain.user.entity.UserEntity
import com.highthon.challenge.global.entity.BaseTimeEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint
import java.time.LocalDateTime

@Entity
@Table(
    name = "badges",
    uniqueConstraints = [UniqueConstraint(columnNames = ["user_id", "type"])],
)
class BadgeEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    val user: UserEntity,

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    val type: BadgeType,

    @Column(name = "granted_at", nullable = false)
    val grantedAt: LocalDateTime = LocalDateTime.now(),
) : BaseTimeEntity()
