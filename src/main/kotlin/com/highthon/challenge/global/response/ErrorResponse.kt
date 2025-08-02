package com.highthon.challenge.global.response

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity

data class ErrorResponse(val message: String) {
    companion object {
        fun of(
            message: String,
            status: HttpStatus,
        ): ResponseEntity<ErrorResponse> = ResponseEntity.status(status).body(ErrorResponse(message))
    }
}
