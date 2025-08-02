package com.highthon.challenge.domain.user.service

import com.highthon.challenge.domain.user.dto.request.SignUpRequest
import com.highthon.challenge.domain.user.entity.UserEntity
import com.highthon.challenge.domain.user.exception.UserError
import com.highthon.challenge.domain.user.repository.UserRepository
import com.highthon.challenge.global.exception.CustomException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
) {
    @Transactional
    fun signup(request: SignUpRequest) {
        validateSignupRequest(request)
        validateUsernameAvailability(request.username)

        val userEntity = UserEntity(
            username = request.username,
            password = passwordEncoder.encode(request.password),
        )

        userRepository.save(userEntity)
    }

    @Transactional(readOnly = true)
    fun checkDuplicate(username: String): Boolean = userRepository.existsByUsername(username)

    /**
     * 회원가입 요청의 유효성을 검사합니다.
     *
     * @param request 회원가입 요청 데이터
     * @throws CustomException 유효하지 않은 username 또는 password인 경우
     */
    private fun validateSignupRequest(request: SignUpRequest) {
        if (request.username.isBlank()) {
            throw CustomException(UserError.INVALID_USERNAME)
        }
        if (request.password.isBlank()) {
            throw CustomException(UserError.INVALID_PASSWORD)
        }
    }

    /**
     * 사용자명의 중복 여부를 검사합니다.
     *
     * @param username 검사할 사용자명
     * @throws CustomException 이미 존재하는 사용자명인 경우
     */
    private fun validateUsernameAvailability(username: String) {
        if (userRepository.existsByUsername(username)) {
            throw CustomException(UserError.EMAIL_DUPLICATION)
        }
    }
}
