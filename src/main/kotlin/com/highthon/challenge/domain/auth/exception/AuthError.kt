package com.highthon.challenge.domain.auth.exception

import com.highthon.challenge.global.exception.CustomError
import org.springframework.http.HttpStatus

enum class AuthError(
    override val status: HttpStatus,
    override val message: String,
) : CustomError {
    WRONG_PASSWORD(HttpStatus.BAD_REQUEST, "비밀번호가 틀렸습니다."),
    INVALID_REFRESH_TOKEN(HttpStatus.BAD_REQUEST, "유효하지 않은 리프레시 토큰입니다."),
    EXPIRED_TOKEN(HttpStatus.UNAUTHORIZED, "토큰이 만료되었습니다."),
    INVALID_TOKEN_SIGNATURE(HttpStatus.UNAUTHORIZED, "유효하지 않은 토큰입니다."),
    MALFORMED_TOKEN(HttpStatus.UNAUTHORIZED, "잘못된 형식의 토큰입니다."),
    UNSUPPORTED_TOKEN(HttpStatus.UNAUTHORIZED, "지원하지 않는 토큰입니다."),
    EMPTY_CLAIMS(HttpStatus.UNAUTHORIZED, "토큰의 클레임이 비어있습니다."),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "인증 처리 중 오류가 발생했습니다."),
}
