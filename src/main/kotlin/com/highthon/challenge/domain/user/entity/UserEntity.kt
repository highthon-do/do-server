package com.highthon.challenge.domain.user.entity

import com.highthon.challenge.domain.mission.entity.MissionEntity
import com.highthon.challenge.global.entity.BaseTimeEntity
import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.OneToMany
import jakarta.persistence.Table

@Entity
@Table(name = "users")
class UserEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(nullable = false, unique = true)
    var username: String = "",

    @Column(nullable = false)
    var password: String = "",

    @OneToMany(mappedBy = "writer", cascade = [CascadeType.ALL], orphanRemoval = true, fetch = FetchType.LAZY)
    val missions: MutableList<MissionEntity> = mutableListOf(),
) : BaseTimeEntity() {
    fun addMission(mission: MissionEntity) = missions.add(mission)
}
