package com.highthon.challenge.domain.auth.docs

import com.highthon.challenge.domain.auth.dto.request.LoginRequest
import com.highthon.challenge.domain.auth.dto.request.RefreshRequest
import com.highthon.challenge.global.response.ApiResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.ExampleObject
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.parameters.RequestBody as SwaggerRequestBody
import io.swagger.v3.oas.annotations.responses.ApiResponse as SwaggerApiResponse

object AuthDocs {
    @Operation(
        summary = "로그인",
        description = "아이디와 비밀번호를 통해 로그인합니다.",
        requestBody = SwaggerRequestBody(
            required = true,
            content = [Content(
                schema = Schema(implementation = LoginRequest::class),
                examples = [ExampleObject(
                    name = "로그인 요청 예시",
                    value = """
                    {
                      "username": "testuser",
                      "password": "password123"
                    }
                    """
                )]
            )]
        ),
        responses = [
            SwaggerApiResponse(
                responseCode = "200",
                description = "로그인 성공",
                content = [Content(
                    schema = Schema(implementation = ApiResponse::class)
                )]
            ),
            SwaggerApiResponse(responseCode = "401", description = "인증 실패")
        ]
    )
    annotation class Login

    @Operation(
        summary = "토큰 재발급",
        description = "리프레시 토큰을 통해 엑세스 토큰을 재발급받습니다.",
        requestBody = SwaggerRequestBody(
            required = true,
            content = [Content(
                schema = Schema(implementation = RefreshRequest::class),
                examples = [ExampleObject(
                    name = "토큰 재발급 요청 예시",
                    value = """
                    {
                      "refreshToken": "your-refresh-token"
                    }
                    """
                )]
            )]
        ),
        responses = [
            SwaggerApiResponse(
                responseCode = "200",
                description = "재발급 성공",
                content = [Content(
                    schema = Schema(implementation = ApiResponse::class)
                )]
            ),
            SwaggerApiResponse(responseCode = "401", description = "토큰 만료 또는 유효하지 않음")
        ]
    )
    annotation class Refresh
}
