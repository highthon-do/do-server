package com.highthon.challenge.domain.user.exception

import com.highthon.challenge.global.exception.CustomError
import org.springframework.http.HttpStatus

enum class UserError(
    override val status: HttpStatus,
    override val message: String,
) : CustomError {
    NOT_FOUND(HttpStatus.NOT_FOUND, "유저를 찾을 수 없습니다."),
    EMAIL_DUPLICATION(HttpStatus.CONFLICT, "이미 존재하는 유저네임입니다."),
    INVALID_USERNAME(HttpStatus.BAD_REQUEST, "유효하지 않은 유저네임입니다."),
    INVALID_PASSWORD(HttpStatus.BAD_REQUEST, "유효하지 않은 비밀번호입니다."),
}
