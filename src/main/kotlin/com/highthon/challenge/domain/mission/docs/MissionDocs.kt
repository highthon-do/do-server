package com.highthon.challenge.domain.mission.docs

import io.swagger.v3.oas.annotations.Operation

object MissionDocs {
    @Target(AnnotationTarget.FUNCTION)
    @Retention(AnnotationRetention.RUNTIME)
    @Operation(
        summary = "미션 생성 API",
        description = "사용자가 직접 미션을 생성합니다."
    )
    annotation class CreateMission

    @Target(AnnotationTarget.FUNCTION)
    @Retention(AnnotationRetention.RUNTIME)
    @Operation(
        summary = "AI 기반 미션 생성 API",
        description = "사용자의 최근 챌린지 소감을 기반으로 AI가 새로운 미션을 자동 생성합니다."
    )
    annotation class GenerateMissionFromAi

    @Target(AnnotationTarget.FUNCTION)
    @Retention(AnnotationRetention.RUNTIME)
    @Operation(
        summary = "내 미션 목록 조회 API",
        description = "현재 로그인한 사용자의 모든 미션 목록을 조회합니다."
    )
    annotation class GetMyMissions

    @Target(AnnotationTarget.FUNCTION)
    @Retention(AnnotationRetention.RUNTIME)
    @Operation(
        summary = "전체 공개 미션 조회 API",
        description = "전체 공개된 모든 미션을 조회합니다."
    )
    annotation class GetAllMissions

    @Target(AnnotationTarget.FUNCTION)
    @Retention(AnnotationRetention.RUNTIME)
    @Operation(
        summary = "완료한 미션 목록 조회 API",
        description = "사용자가 완료한 미션들을 조회합니다."
    )
    annotation class GetCompletedMissions

    @Target(AnnotationTarget.FUNCTION)
    @Retention(AnnotationRetention.RUNTIME)
    @Operation(
        summary = "미션 완료 처리 API",
        description = "특정 미션을 완료 상태로 변경합니다."
    )
    annotation class CompleteMission
}
