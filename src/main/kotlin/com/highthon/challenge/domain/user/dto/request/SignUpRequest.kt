package com.highthon.challenge.domain.user.dto.request

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "회원가입 요청 DTO")
data class SignUpRequest(
    @field:Schema(description = "사용자 ID (영문/숫자 조합)", example = "johndoe123")
    val username: String,

    @field:Schema(description = "비밀번호 (8자 이상)", example = "securePassword1!")
    val password: String,
)
