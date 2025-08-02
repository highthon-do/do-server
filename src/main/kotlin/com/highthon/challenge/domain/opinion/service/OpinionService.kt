package com.highthon.challenge.domain.opinion.service

import com.highthon.challenge.domain.mission.exception.MissionError
import com.highthon.challenge.domain.mission.repository.MissionRepository
import com.highthon.challenge.domain.opinion.dto.request.CreateOpinionRequest
import com.highthon.challenge.domain.opinion.dto.response.OpinionResponse
import com.highthon.challenge.domain.opinion.entity.OpinionEntity
import com.highthon.challenge.domain.opinion.event.OpinionWrittenEvent
import com.highthon.challenge.domain.opinion.exception.OpinionError
import com.highthon.challenge.domain.opinion.repository.OpinionRepository
import com.highthon.challenge.global.exception.CustomError
import com.highthon.challenge.global.exception.CustomException
import com.highthon.challenge.global.security.holder.AuthenticationHolder
import org.slf4j.LoggerFactory
import org.springframework.context.ApplicationEventPublisher
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class OpinionService(
    private val missionRepository: MissionRepository,
    private val opinionRepository: OpinionRepository,
    private val authenticationHolder: AuthenticationHolder,
    private val eventPublisher: ApplicationEventPublisher,
) {
    private val log = LoggerFactory.getLogger(this::class.java)

    /**
     * 미션에 대한 새로운 의견을 생성합니다.
     * 미션 작성자만이 의견을 작성할 수 있습니다.
     *
     * @param missionId 의견을 작성할 미션의 ID
     * @param request 의견 생성 요청 정보 (난이도, 느낀 점, 반응)
     * @throws CustomException 미션이 존재하지 않거나 작성 권한이 없는 경우
     */
    @Transactional
    fun createOpinion(
        missionId: Long,
        request: CreateOpinionRequest,
    ) {
        log.info("Creating opinion for mission ID: {}", missionId)
        val writerId = authenticationHolder.getCurrentUserId()
        log.debug("Current user ID: {}", writerId)

        val missionEntity = validateMissionAccessAndGet(missionId, writerId, OpinionError.UNAUTHORIZED_WRITE)
        log.debug("Mission access validated for user ID: {}", writerId)

        val opinionEntity = OpinionEntity(
            mission = missionEntity,
            difficulty = request.difficulty,
            impression = request.impression,
            reaction = request.reaction,
        )

        opinionRepository.save(opinionEntity).also {
            log.debug("Opinion saved successfully for mission ID: {}", missionId)
        }

        eventPublisher.publishEvent(
            OpinionWrittenEvent(
                userId = writerId,
                missionId = missionId,
            )
        ).also {
            log.debug("OpinionWrittenEvent published for user ID: {} and mission ID: {}", writerId, missionId)
        }

        log.info("Successfully created opinion for mission ID: {}", missionId)
    }

    /**
     * 특정 미션에 대한 모든 의견을 조회합니다.
     * 미션 작성자만이 의견을 조회할 수 있습니다.
     *
     * @param missionId 조회할 미션의 ID
     * @return 미션에 대한 의견 목록
     * @throws CustomException 미션이 존재하지 않거나 접근 권한이 없는 경우
     */
    @Transactional(readOnly = true)
    fun getOpinions(missionId: Long): List<OpinionResponse> {
        log.info("Fetching opinions for mission ID: {}", missionId)
        val writerId = authenticationHolder.getCurrentUserId()
        log.debug("Current user ID: {}", writerId)

        val missionEntity = validateMissionAccessAndGet(missionId, writerId, MissionError.UNAUTHORIZED_ACCESS)

        return missionEntity.opinions.map(::toOpinionResponse).also { opinions ->
            log.info("Retrieved {} opinions for mission ID: {}", opinions.size, missionId)
            log.debug("Opinion IDs: {}", opinions.map { it.id })
        }
    }

    /**
     * 미션의 존재 여부와 접근 권한을 확인하고 미션 엔티티를 반환합니다.
     *
     * @param missionId 미션 ID
     * @param writerId 현재 사용자 ID
     * @param unauthorizedError 권한이 없을 경우 발생시킬 에러
     * @return 검증된 미션 엔티티
     * @throws CustomException 미션이 존재하지 않거나 접근 권한이 없는 경우
     */
    private fun validateMissionAccessAndGet(
        missionId: Long,
        writerId: Long,
        unauthorizedError: CustomError,
    ) = missionRepository.findByIdOrNull(missionId)?.also { mission ->
        log.debug("Found mission ID: {} for validation", missionId)

        if (mission.writer.id != writerId) {
            log.warn("Unauthorized access attempt - User ID: {} tried to access mission ID: {}", writerId, missionId)
            throw CustomException(unauthorizedError)
        }

        log.debug("Access validated for user ID: {} to mission ID: {}", writerId, missionId)
    } ?: run {
        log.warn("Mission not found with ID: {}", missionId)
        throw CustomException(MissionError.NOT_FOUND)
    }

    /**
     * 의견 엔티티를 응답 DTO로 변환합니다.
     *
     * @param opinion 변환할 의견 엔티티
     * @return 변환된 의견 응답 DTO
     */
    private fun toOpinionResponse(opinion: OpinionEntity) = OpinionResponse(
        id = opinion.id!!,
        difficulty = opinion.difficulty,
        impression = opinion.impression,
        reaction = opinion.reaction,
        createdAt = opinion.createdAt!!,
        updatedAt = opinion.updatedAt!!,
    ).also {
        log.trace("Converted opinion entity ID: {} to response DTO", opinion.id)
    }
}
