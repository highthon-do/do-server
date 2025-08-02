package com.highthon.challenge.global.security.token.filter

import com.fasterxml.jackson.databind.ObjectMapper
import com.highthon.challenge.global.exception.CustomException
import com.highthon.challenge.global.response.ErrorResponse
import com.highthon.challenge.global.security.token.provider.TokenProvider
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import java.nio.charset.StandardCharsets

@Component
class TokenAuthenticationFilter(
    private val tokenProvider: TokenProvider,
    private val objectMapper: ObjectMapper,
) : OncePerRequestFilter() {
    private val log = LoggerFactory.getLogger(this::class.java)

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        try {
            val token = getToken(request)

            if (token != null) {
                val userId = tokenProvider.getUserId(token)

                val authentication = UsernamePasswordAuthenticationToken(
                    userId,
                    null,
                    listOf(SimpleGrantedAuthority("ROLE_USER"))
                )

                val context = SecurityContextHolder.createEmptyContext()
                context.authentication = authentication
                SecurityContextHolder.setContext(context)
            }

            filterChain.doFilter(request, response)
        } catch (e: CustomException) {
            log.error("Authentication error occurred", e)
            handleCustomException(response, e)
        }
    }

    private fun handleCustomException(response: HttpServletResponse, e: CustomException) {
        response.apply {
            contentType = MediaType.APPLICATION_JSON_VALUE
            characterEncoding = StandardCharsets.UTF_8.name()
            status = e.error.status.value()
            writer.write(
                objectMapper.writeValueAsString(
                    ErrorResponse(e.error.message)
                )
            )
        }
    }

    private fun getToken(request: HttpServletRequest): String? = request.getHeader(HttpHeaders.AUTHORIZATION)
        ?.removePrefix(BEARER_PREFIX)
        ?.trim()
        ?.takeIf { it.isNotBlank() }

    companion object {
        private const val BEARER_PREFIX = "Bearer "
    }
}
