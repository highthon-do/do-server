package com.highthon.challenge.domain.auth.dto.request

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "리프레시 토큰 요청 DTO")
data class RefreshRequest(
    @field:Schema(description = "리프레시 토큰", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6...")
    val refreshToken: String,
)
