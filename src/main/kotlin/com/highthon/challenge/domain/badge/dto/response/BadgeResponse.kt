package com.highthon.challenge.domain.badge.dto.response

import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDateTime

@Schema(description = "사용자가 획득한 뱃지 정보 응답 DTO")
data class BadgeResponse(
    @field:Schema(description = "뱃지 제목", example = "첫 도전자")
    val title: String,

    @field:Schema(description = "뱃지 설명", example = "한 발짝 내딛은 당신")
    val description: String,

    @field:Schema(description = "뱃지를 획득한 시각", example = "2025-08-03T12:00:00")
    val grantedAt: LocalDateTime,
)
