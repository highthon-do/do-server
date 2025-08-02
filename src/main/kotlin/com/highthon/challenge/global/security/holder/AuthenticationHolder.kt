package com.highthon.challenge.global.security.holder

import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component

@Component
class AuthenticationHolder {
    fun getCurrentUserId(): Long {
        val authentication = SecurityContextHolder.getContext().authentication

        return authentication.principal as Long
    }
}
