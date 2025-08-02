package com.highthon.challenge.domain.auth.dto.request

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "로그인 요청 DTO")
data class LoginRequest(
    @field:Schema(description = "사용자의 아이디 (회원가입 시 사용)", example = "johndoe123")
    val username: String,

    @field:Schema(description = "사용자의 비밀번호", example = "securePassword123!")
    val password: String,
)
