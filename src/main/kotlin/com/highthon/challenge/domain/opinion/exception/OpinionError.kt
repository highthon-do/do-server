package com.highthon.challenge.domain.opinion.exception

import com.highthon.challenge.global.exception.CustomError
import org.springframework.http.HttpStatus

enum class OpinionError(
    override val status: HttpStatus,
    override val message: String,
) : CustomError {
    NOT_FOUND(HttpStatus.NOT_FOUND, "소감을 찾을 수 없습니다."),
    UNAUTHORIZED_WRITE(HttpStatus.FORBIDDEN, "해당 소감에 접근할 수 없습니다."),
    NOT_ENOUGH_OPINIONS(HttpStatus.BAD_REQUEST, "미션 생성을 위한 충분한 소감이 없습니다."),
}
