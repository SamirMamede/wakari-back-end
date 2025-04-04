package com.samirmamede.wakari.config

import io.swagger.v3.oas.models.Components
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Contact
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.info.License
import io.swagger.v3.oas.models.security.SecurityRequirement
import io.swagger.v3.oas.models.security.SecurityScheme
import io.swagger.v3.oas.models.servers.Server
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class OpenApiConfig {
    
    @Value("\${server.servlet.context-path:}")
    private lateinit var contextPath: String

    @Bean
    fun openAPI(): OpenAPI {
        return OpenAPI()
            .info(apiInfo())
            .addSecurityItem(SecurityRequirement().addList("Bearer Authentication"))
            .components(
                Components()
                    .addSecuritySchemes("Bearer Authentication", createAPIKeyScheme())
            )
            .addServersItem(Server().url(contextPath).description("Servidor local"))
    }

    private fun apiInfo(): Info {
        return Info()
            .title("Wakari API")
            .description("API para o sistema de gerenciamento de restaurantes Wakari")
            .version("0.0.1")
            .contact(
                Contact()
                    .name("Samir Mamede")
                    .email("anuarsamir@gmail.com")
                    .url("https://github.com/SamirMamede")
            )
            .license(License().name("MIT License").url("https://opensource.org/licenses/MIT"))
    }

    private fun createAPIKeyScheme(): SecurityScheme {
        return SecurityScheme()
            .type(SecurityScheme.Type.HTTP)
            .bearerFormat("JWT")
            .scheme("bearer")
    }
} 