package com.highthon.challenge.domain.badge.enums

enum class BadgeType(
    val title: String,
    val description: String,
    val condition: Condition
) {
    FIRST_CHALLENGER(
        title = "첫 도전자",
        description = "한 발짝 내딘은 당신",
        condition = Condition.MissionCount(count = 1)
    ),

    CONSISTENT_CHALLENGER(
        title = "꾸준한 도전자",
        description = "습관이 되어가는 중",
        condition = Condition.Streak(days = 3)
    ),

    PASSIONATE_CHALLENGER(
        title = "열정 도전자",
        description = "열정이 쌓이고 있다",
        condition = Condition.MissionCount(count = 10)
    ),

    INTERMEDIATE_CHALLENGER(
        title = "도전 중급자",
        description = "중간 단계까지 왔어요",
        condition = Condition.Streak(days = 5)
    ),

    BOLD_CHALLENGER(
        title = "한계를 넘은 자",
        description = "당신은 이제 용기의 상징",
        condition = Condition.Streak(days = 7)
    ),

    MASTER_CHALLENGER(
        title = "도전 마스터",
        description = "무서움을 이긴 자",
        condition = Condition.MissionCount(count = 25)
    ),

    CHALLENGE_LEADER(
        title = "챌린지 리더",
        description = "당신은 모두의 롤모델",
        condition = Condition.TopPercent(percent = 10)
    );

    sealed interface Condition {
        data class MissionCount(val count: Int) : Condition
        data class Streak(val days: Int) : Condition
        data class TopPercent(val percent: Int) : Condition
    }
}
