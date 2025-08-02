package com.highthon.challenge.global.config.security

import com.highthon.challenge.global.security.handler.CustomAccessDeniedHandler
import com.highthon.challenge.global.security.handler.CustomAuthenticationEntryPoint
import com.highthon.challenge.global.security.token.filter.TokenAuthenticationFilter
import org.apache.catalina.webresources.TomcatURLStreamHandlerFactory.disable
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource

@Configuration
class SecurityConfig(private val tokenAuthenticationFilter: TokenAuthenticationFilter) {
    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .cors { it.configurationSource(corsConfigurationSource()) }
            .csrf { it.disable() }
            .formLogin { it.disable() }
            .httpBasic { it.disable() }
            .rememberMe { it.disable() }
            .logout { it.disable() }
            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }

            .authorizeHttpRequests {
                it.requestMatchers(
                    HttpMethod.POST,
                    "/users/signup",
                    "/auth/login",
                    "/auth/refresh",
                ).permitAll()

                it.requestMatchers(
                    HttpMethod.GET,
                    "/users/check-duplicate",
                    "/swagger-ui/**",
                    "/v3/api-docs/**",
                ).permitAll()

                it.anyRequest().authenticated()
            }

            .exceptionHandling {
                it.authenticationEntryPoint(CustomAuthenticationEntryPoint())
                it.accessDeniedHandler(CustomAccessDeniedHandler())
            }

            .addFilterBefore(tokenAuthenticationFilter, UsernamePasswordAuthenticationFilter::class.java)

        return http.build()
    }

    @Bean
    fun corsConfigurationSource(): CorsConfigurationSource {
        val configuration = CorsConfiguration()

        configuration.allowedOriginPatterns = listOf(
            "*",
        )

        configuration.allowedHeaders = listOf(
            "*",
        )

        configuration.allowedMethods = listOf(
            "*",
        )

        configuration.allowCredentials = true
        configuration.maxAge = 3600L

        val urlBasedCorsConfigurationSource = UrlBasedCorsConfigurationSource()

        urlBasedCorsConfigurationSource.registerCorsConfiguration("/**", configuration)

        return urlBasedCorsConfigurationSource
    }

    @Bean
    fun passwordEncoder(): PasswordEncoder = BCryptPasswordEncoder()
}
