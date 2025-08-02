package com.highthon.challenge.domain.badge.docs

import io.swagger.v3.oas.annotations.Operation

object BadgeDocs {
    @Target(AnnotationTarget.FUNCTION)
    @Retention(AnnotationRetention.RUNTIME)
    @Operation(
        summary = "뱃지 목록 조회 API",
        description = "사용자가 획득한 뱃지를 포함하여 전체 뱃지 목록을 조회합니다. " +
                "획득한 뱃지에는 획득 일자가 포함됩니다."
    )
    annotation class GetBadges

    @Target(AnnotationTarget.FUNCTION)
    @Retention(AnnotationRetention.RUNTIME)
    @Operation(
        summary = "뱃지 진행률 조회 API",
        description = "뱃지 진행률을 조회합니다."
    )
    annotation class GetBadgeProgress
}
