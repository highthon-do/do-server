package com.highthon.challenge.global.security.handler

import com.fasterxml.jackson.databind.ObjectMapper
import com.highthon.challenge.global.response.ErrorResponse
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.web.access.AccessDeniedHandler
import org.springframework.stereotype.Component
import java.nio.charset.StandardCharsets

@Component
class CustomAccessDeniedHandler(private val objectMapper: ObjectMapper) : AccessDeniedHandler {
    private val log = LoggerFactory.getLogger(this::class.java)

    override fun handle(
        request: HttpServletRequest,
        response: HttpServletResponse,
        accessDeniedException: AccessDeniedException
    ) {
        log.error(
            "접근 권한이 없는 요청이 감지되었습니다. URI: {}, Exception: {}",
            request.requestURI,
            accessDeniedException.message
        )

        val errorResponse = ErrorResponse("해당 리소스에 대한 접근 권한이 없습니다.")

        response.apply {
            contentType = MediaType.APPLICATION_JSON_VALUE
            characterEncoding = StandardCharsets.UTF_8.name()
            status = HttpStatus.FORBIDDEN.value()
            writer.write(objectMapper.writeValueAsString(errorResponse))
        }
    }
}
