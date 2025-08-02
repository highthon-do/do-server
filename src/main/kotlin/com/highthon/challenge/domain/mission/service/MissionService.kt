package com.highthon.challenge.domain.mission.service

import com.highthon.challenge.domain.mission.dto.request.CreateMissionRequest
import com.highthon.challenge.domain.mission.dto.response.MissionResponse
import com.highthon.challenge.domain.mission.dto.response.MissionSuggestion
import com.highthon.challenge.domain.mission.entity.MissionEntity
import com.highthon.challenge.domain.mission.enums.MissionStatus
import com.highthon.challenge.domain.mission.event.MissionCompletedEvent
import com.highthon.challenge.domain.mission.exception.MissionError
import com.highthon.challenge.domain.mission.repository.MissionRepository
import com.highthon.challenge.domain.opinion.exception.OpinionError
import com.highthon.challenge.domain.opinion.repository.OpinionRepository
import com.highthon.challenge.domain.user.exception.UserError
import com.highthon.challenge.domain.user.repository.UserRepository
import com.highthon.challenge.global.config.openai.OpenAiMissionClient
import com.highthon.challenge.global.exception.CustomException
import com.highthon.challenge.global.security.holder.AuthenticationHolder
import org.springframework.context.ApplicationEventPublisher
import org.springframework.data.domain.PageRequest
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class MissionService(
    private val userRepository: UserRepository,
    private val missionRepository: MissionRepository,
    private val opinionRepository: OpinionRepository,
    private val openAiMissionClient: OpenAiMissionClient,
    private val eventPublisher: ApplicationEventPublisher,
    private val authenticationHolder: AuthenticationHolder,
) {
    @Transactional
    fun createMission(request: CreateMissionRequest) {
        val writerId = authenticationHolder.getCurrentUserId()
        val userEntity = userRepository.findByIdOrNull(writerId) ?: throw CustomException(UserError.NOT_FOUND)

        userEntity.addMission(
            MissionEntity(
                writer = userEntity,
                content = request.content,
                isPrivate = request.isPrivate,
                aiGenerated = request.aiGenerated,
            )
        )
    }

    fun generateMissionFromAi(): List<MissionSuggestion> {
        val writerId = authenticationHolder.getCurrentUserId()

        val recent = opinionRepository.findByWriterIdOrderByCreatedAtDesc(writerId, PageRequest.of(0, 5))

        if (recent.isEmpty()) throw CustomException(OpinionError.NOT_ENOUGH_OPINIONS)

        return openAiMissionClient.generateMissionFromOpinions(recent.reversed()).block()
            ?: throw CustomException(MissionError.AI_GENERATION_FAILED)
    }

    @Transactional(readOnly = true)
    fun getMyMissions(): List<MissionResponse> {
        val writerId = authenticationHolder.getCurrentUserId()

        return missionRepository.findAllByWriterIdAndStatus(
            writerId = writerId,
            status = MissionStatus.IN_PROGRESS,
        ).map { mission ->
            MissionResponse(
                id = mission.id!!,
                content = mission.content,
                level = mission.level,
                isPrivate = mission.isPrivate,
                aiGenerated = mission.aiGenerated,
                status = mission.status,
                createdAt = mission.createdAt!!,
                updatedAt = mission.updatedAt!!,
            )
        }
    }

    @Transactional(readOnly = true)
    fun getAllMissions(): List<MissionResponse> = missionRepository.findPublicMissions().map { mission ->
        MissionResponse(
            id = mission.id!!,
            content = mission.content,
            level = mission.level,
            isPrivate = mission.isPrivate,
            aiGenerated = mission.aiGenerated,
            status = mission.status,
            createdAt = mission.createdAt!!,
            updatedAt = mission.updatedAt!!,
        )
    }

    @Transactional(readOnly = true)
    fun getCompletedMissions(): List<MissionResponse> {
        val writerId = authenticationHolder.getCurrentUserId()

        return missionRepository.findAllByWriterIdAndStatus(
            writerId = writerId,
            status = MissionStatus.COMPLETED,
        ).map { mission ->
            MissionResponse(
                id = mission.id!!,
                content = mission.content,
                level = mission.level,
                isPrivate = mission.isPrivate,
                aiGenerated = mission.aiGenerated,
                status = mission.status,
                createdAt = mission.createdAt!!,
                updatedAt = mission.updatedAt!!,
            )
        }
    }

    @Transactional
    fun completeMission(missionId: Long) {
        val writerId = authenticationHolder.getCurrentUserId()

        val mission = missionRepository.findByIdAndWriterId(
            id = missionId,
            writerId = writerId,
        ) ?: throw CustomException(MissionError.NOT_FOUND)

        mission.status = MissionStatus.COMPLETED

        eventPublisher.publishEvent(
            MissionCompletedEvent(
                userId = writerId,
                missionId = missionId,
            )
        )
    }
}
