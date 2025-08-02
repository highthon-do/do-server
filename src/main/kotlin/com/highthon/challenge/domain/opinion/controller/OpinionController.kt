package com.highthon.challenge.domain.opinion.controller

import com.highthon.challenge.domain.opinion.docs.OpinionDocs
import com.highthon.challenge.domain.opinion.dto.request.CreateOpinionRequest
import com.highthon.challenge.domain.opinion.dto.response.OpinionResponse
import com.highthon.challenge.domain.opinion.service.OpinionService
import com.highthon.challenge.global.response.ApiResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/missions/{missionId}/opinions")
class OpinionController(private val opinionService: OpinionService) {
    @PostMapping
    @OpinionDocs.CreateOpinion
    fun createOpinion(
        @PathVariable missionId: Long,
        @RequestBody request: CreateOpinionRequest,
    ): ResponseEntity<ApiResponse<Unit>> {
        opinionService.createOpinion(
            missionId = missionId,
            request = request,
        )

        return ApiResponse.created("성공적으로 소감을 생성했습니다.")
    }

    @GetMapping
    @OpinionDocs.GetOpinions
    fun getOpinions(@PathVariable missionId: Long): ResponseEntity<ApiResponse<List<OpinionResponse>>> = ApiResponse.ok(
        data = opinionService.getOpinions(missionId),
        message = "성공적으로 소감 목록을 조회했습니다.",
    )
}
