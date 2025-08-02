package com.highthon.challenge.domain.opinion.entity

import com.highthon.challenge.domain.mission.entity.MissionEntity
import com.highthon.challenge.global.entity.BaseTimeEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table

@Entity
@Table(name = "opinions")
class OpinionEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mission_id", nullable = false)
    val mission: MissionEntity = MissionEntity(),

    @Column(nullable = false)
    var difficulty: String = "",

    @Column(nullable = false)
    var impression: String = "",

    @Column(nullable = false)
    var reaction: String = "",
) : BaseTimeEntity()
