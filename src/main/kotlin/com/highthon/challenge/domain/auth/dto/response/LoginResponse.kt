package com.highthon.challenge.domain.auth.dto.response

data class LoginResponse(
    val tokenResponse: TokenResponse,
    val username: String,
)
