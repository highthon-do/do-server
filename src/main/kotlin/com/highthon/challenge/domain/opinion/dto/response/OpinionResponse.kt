package com.highthon.challenge.domain.opinion.dto.response

import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDateTime

@Schema(description = "소감 응답 DTO")
data class OpinionResponse(
    @field:Schema(description = "소감 ID", example = "1")
    val id: Long,

    @field:Schema(description = "미션의 난이도에 대한 체감", example = "조금 어려웠다")
    val difficulty: String,

    @field:Schema(description = "미션을 수행하며 느낀 점", example = "처음엔 긴장됐지만 해보니 괜찮았다")
    val impression: String,

    @field:Schema(description = "상대방의 반응", example = "친구가 웃어주었다")
    val reaction: String,

    @field:Schema(description = "생성 시간", example = "2025-08-03T12:00:00")
    val createdAt: LocalDateTime,

    @field:Schema(description = "수정 시간", example = "2025-08-03T12:10:00")
    val updatedAt: LocalDateTime,
)
