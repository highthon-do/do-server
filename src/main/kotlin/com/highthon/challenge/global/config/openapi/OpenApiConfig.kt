package com.highthon.challenge.global.config.openapi

import io.swagger.v3.oas.models.Components
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.security.SecurityRequirement
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class OpenApiConfig {
    @Bean
    fun openAPI(): OpenAPI = OpenAPI()
        .components(Components())
        .info(info())
        .addSecurityItem(SecurityRequirement().addList("Bearer Authentication"))

    private fun info(): Info = Info()
        .title("D.O. API")
        .description("D.O. API")
        .version("1.0.0")
}
