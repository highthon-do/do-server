package com.highthon.challenge.domain.mission.entity

import com.highthon.challenge.domain.mission.enums.MissionStatus
import com.highthon.challenge.domain.opinion.entity.OpinionEntity
import com.highthon.challenge.domain.user.entity.UserEntity
import com.highthon.challenge.global.entity.BaseTimeEntity
import jakarta.persistence.CascadeType
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.OneToMany
import jakarta.persistence.Table

@Entity
@Table(name = "missions")
class MissionEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "writer_id")
    val writer: UserEntity = UserEntity(),

    var content: String = "",

    var level: Int = 1,

    var isPrivate: Boolean = true,

    val aiGenerated: Boolean = false,

    @Enumerated(EnumType.STRING)
    var status: MissionStatus = MissionStatus.IN_PROGRESS,

    @OneToMany(mappedBy = "mission", cascade = [CascadeType.ALL], orphanRemoval = true, fetch = FetchType.LAZY)
    val opinions: MutableList<OpinionEntity> = mutableListOf(),
) : BaseTimeEntity()
