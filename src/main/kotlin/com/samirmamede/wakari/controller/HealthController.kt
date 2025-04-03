package com.samirmamede.wakari.controller

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDateTime
import java.util.*

@RestController
@RequestMapping("/health")
class HealthController {

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