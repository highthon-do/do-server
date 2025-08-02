package com.highthon.challenge.domain.mission.repository

import com.highthon.challenge.domain.mission.entity.MissionEntity
import com.highthon.challenge.domain.mission.enums.MissionStatus
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface MissionRepository : JpaRepository<MissionEntity, Long> {
    fun findAllByWriterIdAndStatus(
        writerId: Long,
        status: MissionStatus,
    ): List<MissionEntity>

    @Query("SELECT m FROM MissionEntity m WHERE m.isPrivate = false")
    fun findPublicMissions(): List<MissionEntity>

    fun countByWriterId(writerId: Long): Long

    @Query(
        value = "SELECT DISTINCT DATE(m.created_at) FROM missions m WHERE m.writer_id = :writerId",
        nativeQuery = true
    )
    fun findMissionDatesByWriterId(@Param("writerId") writerId: Long): List<java.sql.Date>

    fun findByIdAndWriterId(
        id: Long,
        writerId: Long,
    ): MissionEntity?

    @Query("SELECT m.writer.id, COUNT(m) FROM MissionEntity m GROUP BY m.writer.id")
    fun findAllUserMissionCounts(): List<Pair<Long, Long>>
}
