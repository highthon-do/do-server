package com.highthon.challenge.domain.user.controller

import com.highthon.challenge.domain.user.docs.UserDocs
import com.highthon.challenge.domain.user.dto.request.SignUpRequest
import com.highthon.challenge.domain.user.service.UserService
import com.highthon.challenge.global.response.ApiResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/users")
class UserController(private val userService: UserService) {
    @PostMapping("/signup")
    @UserDocs.SignUp
    fun signup(@RequestBody request: SignUpRequest): ResponseEntity<ApiResponse<Unit>> {
        userService.signup(request)

        return ApiResponse.created("성공적으로 회원가입했습니다.")
    }

    @GetMapping("/check-duplicate")
    @UserDocs.CheckDuplicate
    fun checkDuplicate(@RequestParam username: String): ResponseEntity<ApiResponse<Boolean>> {
        return ApiResponse.ok(
            data = userService.checkDuplicate(username),
            message = "성공적으로 유저네임 중복을 확인했습니다.",
        )
    }
}
