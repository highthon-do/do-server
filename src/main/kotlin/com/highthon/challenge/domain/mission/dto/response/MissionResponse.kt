package com.highthon.challenge.domain.mission.dto.response

import com.highthon.challenge.domain.mission.enums.MissionStatus
import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDateTime

@Schema(description = "미션 응답 DTO")
data class MissionResponse(
    @field:Schema(description = "미션 ID", example = "1")
    val id: Long,

    @field:Schema(description = "미션 내용", example = "친구에게 인사해보기")
    val content: String,

    @field:Schema(description = "미션 난이도 (0~5)", example = "2")
    val level: Int,

    @field:Schema(description = "비공개 여부", example = "false")
    val isPrivate: Boolean,

    @field:Schema(description = "AI 생성 여부", example = "true")
    val aiGenerated: Boolean,

    @field:Schema(description = "미션 상태 (진행 중, 완료 등)", example = "IN_PROGRESS")
    val status: MissionStatus,

    @field:Schema(description = "미션 생성 시각", example = "2025-08-03T00:00:00")
    val createdAt: LocalDateTime,

    @field:Schema(description = "미션 수정 시각", example = "2025-08-03T00:10:00")
    val updatedAt: LocalDateTime,
)
