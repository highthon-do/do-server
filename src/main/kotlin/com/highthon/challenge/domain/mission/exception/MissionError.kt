package com.highthon.challenge.domain.mission.exception

import com.highthon.challenge.global.exception.CustomError
import org.springframework.http.HttpStatus

enum class MissionError(
    override val status: HttpStatus,
    override val message: String,
) : CustomError {
    NOT_FOUND(HttpStatus.NOT_FOUND, "미션을 찾을 수 없습니다."),
    UNAUTHORIZED_ACCESS(HttpStatus.FORBIDDEN, "이 미션에 접근할 권한이 없습니다."),
    AI_GENERATION_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "AI가 미션을 생성하지 못했습니다."),
}
