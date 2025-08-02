package com.highthon.challenge.domain.opinion.dto.projection

interface MissionOpinionProjection {
    val content: String
    val level: Int
    val difficulty: String
    val impression: String
    val reaction: String
}
