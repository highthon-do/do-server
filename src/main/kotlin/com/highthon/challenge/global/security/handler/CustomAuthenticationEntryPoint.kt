package com.highthon.challenge.global.security.handler

import com.fasterxml.jackson.databind.ObjectMapper
import com.highthon.challenge.global.response.ErrorResponse
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.stereotype.Component
import java.nio.charset.StandardCharsets

@Component
class CustomAuthenticationEntryPoint(private val objectMapper: ObjectMapper) : AuthenticationEntryPoint {
    private val log = LoggerFactory.getLogger(this::class.java)

    override fun commence(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authException: AuthenticationException
    ) {
        log.error(
            "인증되지 않은 접근이 감지되었습니다. URI: {}, Exception: {}",
            request.requestURI,
            authException.message
        )

        val errorResponse = ErrorResponse(message = "인증되지 않은 접근입니다.")

        response.apply {
            contentType = MediaType.APPLICATION_JSON_VALUE
            characterEncoding = StandardCharsets.UTF_8.name()
            status = HttpStatus.UNAUTHORIZED.value()
            writer.write(objectMapper.writeValueAsString(errorResponse))
        }
    }
}
