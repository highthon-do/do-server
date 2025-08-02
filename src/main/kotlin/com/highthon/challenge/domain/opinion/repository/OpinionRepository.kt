package com.highthon.challenge.domain.opinion.repository

import com.highthon.challenge.domain.opinion.dto.projection.MissionOpinionProjection
import com.highthon.challenge.domain.opinion.entity.OpinionEntity
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface OpinionRepository : JpaRepository<OpinionEntity, Long> {
    @Query(
        """
        SELECT 
            m.content AS content,
            m.level AS level,
            o.difficulty AS difficulty,
            o.impression AS impression,
            o.reaction AS reaction
        FROM OpinionEntity o
        JOIN o.mission m
        WHERE m.writer.id = :writerId
        ORDER BY o.createdAt DESC
        """
    )
    fun findByWriterIdOrderByCreatedAtDesc(
        writerId: Long,
        pageable: Pageable,
    ): List<MissionOpinionProjection>
}
