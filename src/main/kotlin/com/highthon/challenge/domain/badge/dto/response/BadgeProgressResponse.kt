package com.highthon.challenge.domain.badge.dto.response

import java.time.LocalDateTime

data class BadgeProgressResponse(
    val title: String,
    val description: String,
    val progress: Int,
    val achieved: Boolean,
    val grantedAt: LocalDateTime?,
)
