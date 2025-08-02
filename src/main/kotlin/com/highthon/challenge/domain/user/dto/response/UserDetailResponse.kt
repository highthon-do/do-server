package com.highthon.challenge.domain.user.dto.response

import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDateTime

@Schema(description = "사용자 상세 응답 DTO")
data class UserDetailResponse(
    @field:Schema(description = "사용자 ID", example = "1")
    val id: Long,

    @field:Schema(description = "사용자 이름", example = "johndoe")
    val username: String,

    @field:Schema(description = "생성 시각", example = "2025-08-01T12:34:56")
    val createdAt: LocalDateTime,

    @field:Schema(description = "수정 시각", example = "2025-08-02T15:12:00")
    val updatedAt: LocalDateTime,
)
