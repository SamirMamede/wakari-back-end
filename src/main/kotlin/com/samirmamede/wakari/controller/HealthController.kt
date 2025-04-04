package com.samirmamede.wakari.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDateTime
import java.util.*

@RestController
@RequestMapping("/health")
@Tag(name = "Diagnóstico", description = "Endpoint para verificar o status da API")
class HealthController {

    @Operation(
        summary = "Verifica a saúde da aplicação",
        description = "Retorna o status atual da API, incluindo versão e timestamp"
    )
    @ApiResponses(
        ApiResponse(responseCode = "200", description = "Aplicação está funcionando normalmente")
    )
    @GetMapping
    fun checkHealth(): ResponseEntity<Map<String, Any>> {
        val status = mapOf(
            "status" to "UP",
            "timestamp" to LocalDateTime.now().toString(),
            "app" to "Wakari API",
            "version" to "0.0.1"
        )
        
        return ResponseEntity.ok(status)
    }
} 