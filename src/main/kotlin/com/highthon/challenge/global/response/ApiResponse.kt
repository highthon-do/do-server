package com.highthon.challenge.global.response

import com.fasterxml.jackson.annotation.JsonInclude
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity

@JsonInclude(JsonInclude.Include.NON_NULL)
data class ApiResponse<T>(
    val data: T? = null,
    val message: String,
) {
    companion object {
        fun <T> of(
            data: T,
            message: String,
            status: HttpStatus,
        ): ResponseEntity<ApiResponse<T>> = ResponseEntity.status(status).body(
            ApiResponse(
                data = data,
                message = message,
            )
        )

        fun of(
            message: String,
            status: HttpStatus,
        ): ResponseEntity<ApiResponse<Unit>> = ResponseEntity.status(status).body(ApiResponse(message = message))

        fun <T> ok(
            data: T,
            message: String,
        ): ResponseEntity<ApiResponse<T>> = of(
            data = data,
            message = message,
            status = HttpStatus.OK,
        )

        fun ok(message: String): ResponseEntity<ApiResponse<Unit>> = of(
            message = message,
            status = HttpStatus.OK,
        )

        fun <T> created(
            data: T,
            message: String,
        ): ResponseEntity<ApiResponse<T>> = of(
            data = data,
            message = message,
            status = HttpStatus.CREATED,
        )

        fun created(message: String): ResponseEntity<ApiResponse<Unit>> = of(
            message = message,
            status = HttpStatus.CREATED,
        )
    }
}
