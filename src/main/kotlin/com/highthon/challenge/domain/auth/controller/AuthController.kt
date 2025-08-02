package com.highthon.challenge.domain.auth.controller

import com.highthon.challenge.domain.auth.docs.AuthDocs
import com.highthon.challenge.domain.auth.dto.request.LoginRequest
import com.highthon.challenge.domain.auth.dto.request.RefreshRequest
import com.highthon.challenge.domain.auth.dto.response.LoginResponse
import com.highthon.challenge.domain.auth.dto.response.TokenResponse
import com.highthon.challenge.domain.auth.service.AuthService
import com.highthon.challenge.global.response.ApiResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/auth")
class AuthController(private val authService: AuthService) {
    @PostMapping("/login")
    @AuthDocs.Login
    fun login(@RequestBody request: LoginRequest): ResponseEntity<ApiResponse<LoginResponse>> {
        return ApiResponse.ok(
            data = authService.login(request),
            message = "성공적으로 로그인했습니다.",
        )
    }

    @PostMapping("/refresh")
    @AuthDocs.Refresh
    fun refresh(@RequestBody request: RefreshRequest): ResponseEntity<ApiResponse<TokenResponse>> {
        return ApiResponse.ok(
            data = authService.refresh(request),
            message = "성공적으로 토큰을 재발급했습니다.",
        )
    }
}
