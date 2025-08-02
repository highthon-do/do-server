package com.highthon.challenge.domain.user.docs

import io.swagger.v3.oas.annotations.Operation

object UserDocs {
    @Target(AnnotationTarget.FUNCTION)
    @Retention(AnnotationRetention.RUNTIME)
    @Operation(
        summary = "회원가입 API",
        description = "사용자로 회원가입합니다."
    )
    annotation class SignUp

    @Target(AnnotationTarget.FUNCTION)
    @Retention(AnnotationRetention.RUNTIME)
    @Operation(
        summary = "유저네임 중복 체크 API",
        description = "유저네임이 중복되는지 확입합니다."
    )
    annotation class CheckDuplicate
}
