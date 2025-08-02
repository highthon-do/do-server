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
import org.slf4j.LoggerFactory
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
    private val log = LoggerFactory.getLogger(this::class.java)

    /**
     * 새로운 미션을 생성합니다.
     * @param request 미션 생성 요청 데이터
     * @throws CustomException 사용자를 찾을 수 없는 경우 (UserError.NOT_FOUND)
     */
    @Transactional
    fun createMission(request: CreateMissionRequest) {
        val writerId = authenticationHolder.getCurrentUserId()
        log.info("Creating new mission for user ID: $writerId")

        val userEntity = userRepository.findByIdOrNull(writerId) ?: run {
            log.error("User not found with ID: $writerId")
            throw CustomException(UserError.NOT_FOUND)
        }

        userEntity.addMission(
            MissionEntity(
                writer = userEntity,
                content = request.content,
                isPrivate = request.isPrivate,
                aiGenerated = request.aiGenerated,
            )
        )
        log.info("Successfully created mission for user ID: $writerId")
    }

    /**
     * AI를 통해 새로운 미션을 생성합니다.
     * @return 생성된 미션 제안 목록
     * @throws CustomException 충분한 의견이 없거나 AI 생성에 실패한 경우
     */
    fun generateMissionFromAi(): List<MissionSuggestion> {
        val writerId = authenticationHolder.getCurrentUserId()
        log.info("Generating AI missions for user ID: $writerId")

        val recent = opinionRepository.findByWriterIdOrderByCreatedAtDesc(writerId, PageRequest.of(0, 5))

        if (recent.isEmpty()) {
            log.warn("Not enough opinions for user ID: $writerId")
            throw CustomException(OpinionError.NOT_ENOUGH_OPINIONS)
        }

        return openAiMissionClient.generateMissionFromOpinions(recent.reversed()).block()
            ?: throw CustomException(MissionError.AI_GENERATION_FAILED)
    }

    /**
     * 현재 진행 중인 사용자의 미션 목록을 조회합니다.
     * @return 진행 중인 미션 목록
     */
    @Transactional(readOnly = true)
    fun getMyMissions(): List<MissionResponse> {
        val writerId = authenticationHolder.getCurrentUserId()
        log.info("Fetching in-progress missions for user ID: $writerId")

        return missionRepository.findAllByWriterIdAndStatus(
            writerId = writerId,
            status = MissionStatus.IN_PROGRESS,
        ).map(::toMissionResponse).also {
            log.info("Found ${it.size} in-progress missions for user ID: $writerId")
        }
    }

    /**
     * 모든 공개된 미션 목록을 조회합니다.
     * @return 공개된 미션 목록
     */
    @Transactional(readOnly = true)
    fun getAllMissions(): List<MissionResponse> = missionRepository.findPublicMissions().map(::toMissionResponse).also {
        log.info("Fetched ${it.size} public missions")
    }

    /**
     * 사용자의 완료된 미션 목록을 조회합니다.
     * @return 완료된 미션 목록
     */
    @Transactional(readOnly = true)
    fun getCompletedMissions(): List<MissionResponse> {
        val writerId = authenticationHolder.getCurrentUserId()
        log.info("Fetching completed missions for user ID: $writerId")

        return missionRepository.findAllByWriterIdAndStatus(
            writerId = writerId,
            status = MissionStatus.COMPLETED,
        ).map(::toMissionResponse).also {
            log.info("Found ${it.size} completed missions for user ID: $writerId")
        }
    }

    /**
     * 특정 미션을 완료 상태로 변경합니다.
     * @param missionId 완료할 미션의 ID
     * @throws CustomException 미션을 찾을 수 없는 경우 (MissionError.NOT_FOUND)
     */
    @Transactional
    fun completeMission(missionId: Long) {
        val writerId = authenticationHolder.getCurrentUserId()
        log.info("Completing mission ID: $missionId for user ID: $writerId")

        val mission = missionRepository.findByIdAndWriterId(
            id = missionId,
            writerId = writerId,
        ) ?: run {
            log.error("Mission not found - ID: $missionId, User ID: $writerId")
            throw CustomException(MissionError.NOT_FOUND)
        }

        mission.status = MissionStatus.COMPLETED

        eventPublisher.publishEvent(
            MissionCompletedEvent(
                userId = writerId,
                missionId = missionId,
            )
        )
        log.info("Successfully completed mission ID: $missionId for user ID: $writerId")
    }

    /**
     * MissionEntity를 MissionResponse로 변환합니다.
     * @param mission 변환할 미션 엔티티
     * @return 변환된 미션 응답 객체
     */
    private fun toMissionResponse(mission: MissionEntity) = MissionResponse(
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
