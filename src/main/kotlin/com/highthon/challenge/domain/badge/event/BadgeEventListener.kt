package com.highthon.challenge.domain.badge.event

import com.highthon.challenge.domain.badge.service.BadgeService
import com.highthon.challenge.domain.mission.event.MissionCompletedEvent
import com.highthon.challenge.domain.opinion.event.OpinionWrittenEvent
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component

@Component
class BadgeEventListener(private val badgeService: BadgeService) {
    @EventListener
    fun handleMissionCompleted(event: MissionCompletedEvent) {
        badgeService.evaluateAndGrantBadges(event.userId)
    }

    @EventListener
    fun handleOpinionWritten(event: OpinionWrittenEvent) {
        badgeService.evaluateAndGrantBadges(event.userId)
    }
}
