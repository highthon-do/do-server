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
import org.slf4j.LoggerFactory
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
    private val log = LoggerFactory.getLogger(this::class.java)

    /**
     * 사용자 로그인을 처리합니다.
     * 유효한 인증 정보인 경우 액세스 토큰과 리프레시 토큰을 발급합니다.
     *
     * @param request 로그인 요청 정보 (username, password)
     * @return 토큰 정보와 사용자 이름을 포함한 로그인 응답
     * @throws CustomException 인증 실패 시 (UserError.NOT_FOUND, AuthError.WRONG_PASSWORD)
     */
    @Transactional(readOnly = true)
    fun login(request: LoginRequest): LoginResponse {
        log.info("Attempting login for user: {}", request.username)

        val userEntity = validateUserCredentials(request.username, request.password)
        log.debug("User credentials validated successfully for: {}", request.username)

        val tokenResponse = tokenProvider.generateTokens(userEntity.id!!)
        log.info("Login successful for user: {}", request.username)

        return LoginResponse(
            tokenResponse = tokenResponse,
            username = request.username,
        )
    }

    /**
     * 리프레시 토큰을 사용하여 새로운 액세스 토큰을 발급합니다.
     *
     * @param request 리프레시 토큰 정보
     * @return 새로 발급된 액세스 토큰과 리프레시 토큰
     * @throws CustomException 리프레시 토큰이 유효하지 않거나 사용자를 찾을 수 없는 경우
     */
    @Transactional(readOnly = true)
    fun refresh(request: RefreshRequest): TokenResponse {
        log.info("Attempting to refresh tokens")

        val userId = validateRefreshToken(request.refreshToken)
        log.debug("Refresh token validated successfully for user ID: {}", userId)

        val tokenResponse = tokenProvider.generateTokens(userId)
        log.info("Tokens refreshed successfully for user ID: {}", userId)

        return tokenResponse
    }

    /**
     * 사용자 인증 정보를 검증합니다.
     *
     * @param username 사용자 이름
     * @param password 비밀번호
     * @return 검증된 사용자 엔티티
     * @throws CustomException 사용자를 찾을 수 없거나 비밀번호가 일치하지 않는 경우
     */
    private fun validateUserCredentials(username: String, password: String) =
        userRepository.findByUsername(username)?.also { user ->
            log.debug("Found user entity for username: {}", username)

            if (!passwordEncoder.matches(password, user.password)) {
                log.warn("Password mismatch for user: {}", username)
                throw CustomException(AuthError.WRONG_PASSWORD)
            }

            log.debug("Password validated successfully for user: {}", username)
        } ?: run {
            log.warn("User not found with username: {}", username)
            throw CustomException(UserError.NOT_FOUND)
        }

    /**
     * Refresh 토큰을 검증하고 해당하는 사용자 ID를 반환합니다.
     *
     * @param refreshToken 검증할 refresh 토큰
     * @return 검증된 사용자 ID
     * @throws CustomException 토큰이 유효하지 않거나 사용자를 찾을 수 없는 경우
     */
    private fun validateRefreshToken(refreshToken: String): Long {
        log.debug("Validating refresh token")

        if (tokenProvider.getTokenPurpose(refreshToken) != TokenPurpose.REFRESH) {
            log.warn("Invalid token purpose for refresh token")
            throw CustomException(AuthError.INVALID_REFRESH_TOKEN)
        }

        val userId = tokenProvider.getUserId(refreshToken)
        log.debug("Extracted user ID from refresh token: {}", userId)

        if (!refreshTokenStore.findByUserId(userId).equals(refreshToken)) {
            log.warn("Stored refresh token doesn't match for user ID: {}", userId)
            throw CustomException(AuthError.INVALID_REFRESH_TOKEN)
        }

        if (!userRepository.existsById(userId)) {
            log.warn("User not found for ID: {}", userId)
            throw CustomException(UserError.NOT_FOUND)
        }

        log.debug("Refresh token validated successfully for user ID: {}", userId)
        return userId
    }
}
