package com.highthon.challenge.domain.user.dto.response

import io.swagger.v3.oas.annotations.media.Schema


@Schema(description = "사용자 응답 DTO")
data class UserResponse(
    @field:Schema(description = "사용자 ID", example = "1")
    val id: Long,

    @field:Schema(description = "사용자 이름", example = "johndoe")
    val username: String,
)
