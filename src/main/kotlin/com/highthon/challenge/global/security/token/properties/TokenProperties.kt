package com.highthon.challenge.global.security.token.properties

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "token")
data class TokenProperties(
    val issuer: String,
    val secret: String,
    val expiration: Expiration,
) {
    data class Expiration(
        val access: Long,
        val refresh: Long,
    )
}
