package com.highthon.challenge.domain.opinion.docs

import io.swagger.v3.oas.annotations.Operation

object OpinionDocs {
    @Target(AnnotationTarget.FUNCTION)
    @Retention(AnnotationRetention.RUNTIME)
    @Operation(
        summary = "소감 작성 API",
        description = "특정 미션에 대한 소감을 작성합니다."
    )
    annotation class CreateOpinion

    @Target(AnnotationTarget.FUNCTION)
    @Retention(AnnotationRetention.RUNTIME)
    @Operation(
        summary = "소감 목록 조회 API",
        description = "특정 미션에 대한 모든 소감 목록을 조회합니다."
    )
    annotation class GetOpinions
}
