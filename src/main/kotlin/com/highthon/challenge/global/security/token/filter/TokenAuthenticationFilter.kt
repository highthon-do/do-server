package com.highthon.challenge.global.security.token.filter

import com.highthon.challenge.global.security.token.provider.TokenProvider
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpHeaders
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class TokenAuthenticationFilter(private val tokenProvider: TokenProvider) : OncePerRequestFilter() {
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
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
    }

    private fun getToken(request: HttpServletRequest): String? = request.getHeader(HttpHeaders.AUTHORIZATION)
        ?.removePrefix(BEARER_PREFIX)
        ?.trim()
        ?.takeIf { it.isNotBlank() }

    companion object {
        private const val BEARER_PREFIX = "Bearer "
    }
}
