package com.highthon.challenge.domain.opinion.dto.request

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "소감 생성 요청 DTO")
data class CreateOpinionRequest(
    @field:Schema(description = "미션의 난이도에 대한 체감", example = "조금 어려웠다")
    val difficulty: String,

    @field:Schema(description = "미션을 수행하며 느낀 점", example = "처음엔 긴장됐지만 해보니 괜찮았다")
    val impression: String,

    @field:Schema(description = "상대방의 반응", example = "친구가 웃어주었다")
    val reaction: String,
)
