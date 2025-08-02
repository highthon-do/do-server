package com.highthon.challenge.domain.auth.service

import com.highthon.challenge.domain.auth.dto.request.LoginRequest
import com.highthon.challenge.domain.auth.dto.request.RefreshRequest
import com.highthon.challenge.domain.auth.dto.response.LoginResponse
import com.highthon.challenge.domain.auth.dto.response.TokenResponse
import com.highthon.challenge.domain.auth.enums.TokenPurpose
import com.highthon.challenge.domain.auth.exception.AuthError
import com.highthon.challenge.domain.auth.store.RefreshTokenStore
import com.highthon.challenge.domain.user.exception.UserError
import com.highthon.challenge.domain.user.repository.UserRepository
import com.highthon.challenge.global.exception.CustomException
import com.highthon.challenge.global.security.token.provider.TokenProvider
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class AuthService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val tokenProvider: TokenProvider,
    private val refreshTokenStore: RefreshTokenStore,
) {
    @Transactional(readOnly = true)
    fun login(request: LoginRequest): LoginResponse {
        val username = request.username
        val password = request.password

        val userEntity = userRepository.findByUsername(username) ?: throw CustomException(UserError.NOT_FOUND)

        if (!passwordEncoder.matches(password, userEntity.password)) {
            throw CustomException(AuthError.WRONG_PASSWORD)
        }

        val tokenResponse = tokenProvider.generateTokens(userEntity.id!!)

        return LoginResponse(
            tokenResponse = tokenResponse,
            username = username,
        )
    }

    @Transactional(readOnly = true)
    fun refresh(request: RefreshRequest): TokenResponse {
        val refreshToken = request.refreshToken

        if (tokenProvider.getTokenPurpose(refreshToken) != TokenPurpose.REFRESH) {
            throw CustomException(AuthError.INVALID_REFRESH_TOKEN)
        }

        val userId = tokenProvider.getUserId(refreshToken)

        if (!refreshTokenStore.findByUserId(userId).equals(request.refreshToken)) {
            throw CustomException(AuthError.INVALID_REFRESH_TOKEN)
        }

        if (!userRepository.existsById(userId)) {
            throw CustomException(UserError.NOT_FOUND)
        }

        return tokenProvider.generateTokens(userId)
    }
}
