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
        val username = request.username
        val password = request.password

        if (username.isBlank()) {
            throw CustomException(UserError.INVALID_USERNAME)
        }

        if (password.isBlank()) {
            throw CustomException(UserError.INVALID_PASSWORD)
        }

        if (userRepository.existsByUsername(username)) {
            throw CustomException(UserError.EMAIL_DUPLICATION)
        }

        val userEntity = UserEntity(
            username = username,
            password = passwordEncoder.encode(password),
        )

        userRepository.save(userEntity)
    }

    @Transactional(readOnly = true)
    fun checkDuplicate(username: String): Boolean = userRepository.existsByUsername(username)
}
