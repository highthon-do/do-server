package com.highthon.challenge.domain.mission.dto.request

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "미션 생성 요청 DTO")
data class CreateMissionRequest(
    @field:Schema(description = "미션 내용", example = "친구에게 인사해보기")
    val content: String,

    @field:Schema(description = "미션 난이도 (0~5)", example = "1")
    val level: Int,

    @field:Schema(description = "비공개 여부", example = "false")
    val isPrivate: Boolean,

    @field:Schema(description = "AI 생성 여부", example = "true")
    val aiGenerated: Boolean,
)
