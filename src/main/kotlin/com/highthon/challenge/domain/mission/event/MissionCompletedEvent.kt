package com.highthon.challenge.domain.mission.event

data class MissionCompletedEvent(
    val userId: Long,
    val missionId: Long,
)
