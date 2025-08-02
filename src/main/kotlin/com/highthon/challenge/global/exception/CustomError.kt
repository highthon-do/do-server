package com.highthon.challenge.global.exception

import org.springframework.http.HttpStatus

interface CustomError {
    val message: String
    val status: HttpStatus
}
