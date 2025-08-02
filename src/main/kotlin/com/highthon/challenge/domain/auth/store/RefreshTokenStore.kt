package com.highthon.challenge.domain.auth.store

import com.highthon.challenge.global.security.token.properties.TokenProperties
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Repository
import java.util.concurrent.TimeUnit

@Repository
class RefreshTokenStore(
    private val redisTemplate: RedisTemplate<String, String>,
    private val tokenProperties: TokenProperties,
) {
    fun store(
        userId: Long,
        refreshToken: String,
    ): Unit = redisTemplate.opsForValue().set(
        KEY_PREFIX + userId,
        refreshToken,
        tokenProperties.expiration.refresh,
        TimeUnit.SECONDS,
    )

    fun findByUserId(userId: Long): String? = redisTemplate.opsForValue().get(KEY_PREFIX + userId)

    fun existsByUserId(userId: Long): Boolean = redisTemplate.hasKey(KEY_PREFIX + userId)

    companion object {
        private const val KEY_PREFIX = "auth:refresh-token:"
    }
}
