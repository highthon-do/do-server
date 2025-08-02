package com.highthon.challenge.domain.badge.controller

import com.highthon.challenge.domain.badge.docs.BadgeDocs
import com.highthon.challenge.domain.badge.dto.response.BadgeProgressResponse
import com.highthon.challenge.domain.badge.dto.response.BadgeResponse
import com.highthon.challenge.domain.badge.service.BadgeService
import com.highthon.challenge.global.response.ApiResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("badges")
class BadgeController(private val badgeService: BadgeService) {
    @GetMapping
    @BadgeDocs.GetBadges
    fun getBadges(): ResponseEntity<ApiResponse<List<BadgeResponse>>> = ApiResponse.ok(
        data = badgeService.getBadges(),
        message = "성공적으로 뱃지 목록을 조회했습니다.",
    )

    @GetMapping("/progress")
    @BadgeDocs.GetBadgeProgress
    fun getBadgeProgress(): ResponseEntity<ApiResponse<List<BadgeProgressResponse>>> = ApiResponse.ok(
        data = badgeService.getBadgeProgress(),
        message = "성공적으로 뱃지 진행률을 조회했습니다.",
    )
}
