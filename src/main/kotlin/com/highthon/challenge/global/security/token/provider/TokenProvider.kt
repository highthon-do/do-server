package com.highthon.challenge.global.security.token.provider

import com.highthon.challenge.domain.auth.dto.response.TokenResponse
import com.highthon.challenge.domain.auth.enums.TokenPurpose
import com.highthon.challenge.global.security.token.properties.TokenProperties
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import org.springframework.stereotype.Service
import java.time.Duration
import java.util.Date

@Service
class TokenProvider(private val tokenProperties: TokenProperties) {
    private val key by lazy { Keys.hmacShaKeyFor(Decoders.BASE64.decode(tokenProperties.secret)) }
    private val parser by lazy { Jwts.parser().verifyWith(key).build() }

    fun generateTokens(userId: Long): TokenResponse {
        val now = Date()
        val accessDuration = Duration.ofSeconds(tokenProperties.expiration.access)
        val refreshDuration = Duration.ofSeconds(tokenProperties.expiration.refresh)
        val accessExpiration = Date(now.time + accessDuration.toMillis())
        val refreshExpiration = Date(now.time + refreshDuration.toMillis())

        val accessToken = buildToken(
            userId = userId,
            now = now,
            expiration = accessExpiration,
            purpose = TokenPurpose.ACCESS,
        )

        val refreshToken = buildToken(
            userId = userId,
            now = now,
            expiration = refreshExpiration,
            purpose = TokenPurpose.REFRESH,
        )

        return TokenResponse(
            accessToken = accessToken,
            refreshToken = refreshToken,
            expiresIn = tokenProperties.expiration.access,
        )
    }

    fun getUserId(token: String): Long = parseClaims(token).subject.toLong()

    fun getTokenPurpose(token: String): TokenPurpose =
        TokenPurpose.valueOf(parseClaims(token)["purpose"].toString().uppercase())

    private fun parseClaims(token: String): Claims = try {
        parser.parseSignedClaims(token).payload
    } catch (e: Exception) {
        throw RuntimeException("토큰을 파싱하는 데 실패했습니다.", e)
    }

    private fun buildToken(
        userId: Long,
        now: Date,
        expiration: Date,
        purpose: TokenPurpose,
    ): String = Jwts.builder()
        .header()
        .type("JWT")
        .and()
        .claims()
        .issuer(tokenProperties.issuer)
        .issuedAt(now)
        .expiration(expiration)
        .subject(userId.toString())
        .add("purpose", purpose.toString().lowercase())
        .and()
        .signWith(key, Jwts.SIG.HS256)
        .compact()
}
