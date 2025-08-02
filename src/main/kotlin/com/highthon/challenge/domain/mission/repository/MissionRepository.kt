package com.highthon.challenge.domain.mission.repository

import com.highthon.challenge.domain.mission.entity.MissionEntity
import com.highthon.challenge.domain.mission.enums.MissionStatus
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
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
        """
        SELECT DATE(m.createdAt) 
        FROM MissionEntity m 
        WHERE m.writer.id = :writerId AND m.isPrivate = false 
        GROUP BY DATE(m.createdAt)
        ORDER BY DATE(m.createdAt) DESC
        """
    )
    fun findMissionDatesByWriterId(writerId: Long): List<java.time.LocalDate>

    @Query(
        """
    SELECT COUNT(*) FROM (
        SELECT writer_id, COUNT(id) AS cnt
        FROM missions
        WHERE is_private = false
        GROUP BY writer_id
        HAVING COUNT(id) > (
            SELECT COUNT(*) * (:percent / 100.0)
            FROM (
                SELECT writer_id
                FROM missions
                WHERE is_private = false
                GROUP BY writer_id
            ) AS total
        )
    ) AS ranked
    WHERE ranked.writer_id = :userId
    """, nativeQuery = true
    )
    fun isUserInTopPercent(userId: Long, percent: Int = 10): Int

    fun findByIdAndWriterId(
        id: Long,
        writerId: Long,
    ): MissionEntity?

    @Query("SELECT m.writer.id, COUNT(m) FROM MissionEntity m GROUP BY m.writer.id")
    fun findAllUserMissionCounts(): List<Pair<Long, Int>>
}
