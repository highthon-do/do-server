package com.highthon.challenge.domain.opinion.service

import com.highthon.challenge.domain.mission.exception.MissionError
import com.highthon.challenge.domain.mission.repository.MissionRepository
import com.highthon.challenge.domain.opinion.dto.request.CreateOpinionRequest
import com.highthon.challenge.domain.opinion.dto.response.OpinionResponse
import com.highthon.challenge.domain.opinion.entity.OpinionEntity
import com.highthon.challenge.domain.opinion.event.OpinionWrittenEvent
import com.highthon.challenge.domain.opinion.exception.OpinionError
import com.highthon.challenge.domain.opinion.repository.OpinionRepository
import com.highthon.challenge.global.exception.CustomException
import com.highthon.challenge.global.security.holder.AuthenticationHolder
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
    @Transactional
    fun createOpinion(
        missionId: Long,
        request: CreateOpinionRequest,
    ) {
        val writerId = authenticationHolder.getCurrentUserId()
        val missionEntity = missionRepository.findByIdOrNull(missionId) ?: throw CustomException(MissionError.NOT_FOUND)

        if (missionEntity.writer.id != writerId) {
            throw CustomException(OpinionError.UNAUTHORIZED_WRITE)
        }

        opinionRepository.save(
            OpinionEntity(
                mission = missionEntity,
                difficulty = request.difficulty,
                impression = request.impression,
                reaction = request.reaction,
            )
        )

        eventPublisher.publishEvent(
            OpinionWrittenEvent(
                userId = writerId,
                missionId = missionId,
            )
        )
    }

    @Transactional(readOnly = true)
    fun getOpinions(missionId: Long): List<OpinionResponse> {
        val writerId = authenticationHolder.getCurrentUserId()
        val missionEntity = missionRepository.findByIdOrNull(missionId) ?: throw CustomException(MissionError.NOT_FOUND)

        if (missionEntity.writer.id != writerId) {
            throw CustomException(MissionError.UNAUTHORIZED_ACCESS)
        }

        return missionEntity.opinions.map { opinion ->
            OpinionResponse(
                id = opinion.id!!,
                difficulty = opinion.difficulty,
                impression = opinion.impression,
                reaction = opinion.reaction,
                createdAt = opinion.createdAt!!,
                updatedAt = opinion.updatedAt!!,
            )
        }
    }
}
