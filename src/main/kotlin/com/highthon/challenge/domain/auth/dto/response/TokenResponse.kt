package com.highthon.challenge.domain.auth.dto.response

data class TokenResponse(
    val accessToken: String,
    val refreshToken: String,
    val expiresIn: Long,
)
