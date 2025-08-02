package com.highthon.challenge.domain.mission.controller

import com.highthon.challenge.domain.mission.docs.MissionDocs
import com.highthon.challenge.domain.mission.dto.request.CreateMissionRequest
import com.highthon.challenge.domain.mission.dto.response.MissionResponse
import com.highthon.challenge.domain.mission.dto.response.MissionSuggestion
import com.highthon.challenge.domain.mission.service.MissionService
import com.highthon.challenge.global.response.ApiResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/missions")
class MissionController(private val missionService: MissionService) {
    @PostMapping
    @MissionDocs.CreateMission
    fun createMission(@RequestBody request: CreateMissionRequest): ResponseEntity<ApiResponse<Unit>> {
        missionService.createMission(request)

        return ApiResponse.created("성공적으로 미션을 생성했습니다.")
    }

    @PostMapping("/ai")
    @MissionDocs.GenerateMissionFromAi
    fun generateMissionFromAi(): ResponseEntity<ApiResponse<List<MissionSuggestion>>> = ApiResponse.ok(
        data = missionService.generateMissionFromAi(),
        message = "성공적으로 미션을 생성했습니다.",
    )

    @GetMapping
    @MissionDocs.GetMyMissions
    fun getMyMissions(): ResponseEntity<ApiResponse<List<MissionResponse>>> = ApiResponse.ok(
        data = missionService.getMyMissions(),
        message = "성공적으로 미션 목록을 조회했습니다.",
    )

    @GetMapping("/public")
    @MissionDocs.GetAllMissions
    fun getAllMissions(): ResponseEntity<ApiResponse<List<MissionResponse>>> = ApiResponse.ok(
        data = missionService.getAllMissions(),
        message = "성공적으로 미션 목록을 조회했습니다.",
    )

    @GetMapping("/completed")
    @MissionDocs.GetCompletedMissions
    fun getCompletedMissions(): ResponseEntity<ApiResponse<List<MissionResponse>>> = ApiResponse.ok(
        data = missionService.getCompletedMissions(),
        message = "성공적으로 미션 목록을 조회했습니다.",
    )

    @PatchMapping("/{missionId}")
    @MissionDocs.CompleteMission
    fun completeMission(@PathVariable missionId: Long): ResponseEntity<ApiResponse<Unit>> {
        missionService.completeMission(missionId)

        return ApiResponse.ok("성공적으로 미션을 완료했습니다.")
    }
}
