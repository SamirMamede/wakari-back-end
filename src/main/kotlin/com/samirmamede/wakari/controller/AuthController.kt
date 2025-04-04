package com.samirmamede.wakari.controller

import com.samirmamede.wakari.dto.AuthRequest
import com.samirmamede.wakari.dto.AuthResponse
import com.samirmamede.wakari.dto.RegisterRequest
import com.samirmamede.wakari.service.AuthService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/auth")
@Tag(name = "Autenticação", description = "Endpoints para autenticação e registro de usuários")
class AuthController(private val authService: AuthService) {
    
    private val logger = LoggerFactory.getLogger(AuthController::class.java)

    @Operation(
        summary = "Registra um novo usuário",
        description = "Registra um novo usuário com nome, email, senha e perfil"
    )
    @ApiResponses(
        ApiResponse(responseCode = "201", description = "Usuário registrado com sucesso"),
        ApiResponse(responseCode = "400", description = "Dados inválidos"),
        ApiResponse(responseCode = "409", description = "Usuário já existe")
    )
    @PostMapping("/register")
    fun register(@Valid @RequestBody request: RegisterRequest): ResponseEntity<AuthResponse> {
        logger.info("Recebida solicitação de registro para o email: ${request.email}")
        
        try {
            val response = authService.register(request)
            logger.info("Usuário registrado com sucesso: ${request.email}")
            return ResponseEntity.status(HttpStatus.CREATED).body(response)
        } catch (e: Exception) {
            logger.error("Erro ao registrar usuário: ${e.message}", e)
            throw e
        }
    }
    
    @Operation(
        summary = "Autentica um usuário",
        description = "Autentica um usuário com email e senha, retornando um token JWT"
    )
    @ApiResponses(
        ApiResponse(responseCode = "200", description = "Autenticação bem-sucedida"),
        ApiResponse(responseCode = "400", description = "Dados inválidos"),
        ApiResponse(responseCode = "401", description = "Credenciais inválidas")
    )
    @PostMapping("/login")
    fun authenticate(@Valid @RequestBody request: AuthRequest): ResponseEntity<AuthResponse> {
        logger.info("Recebida solicitação de login para o email: ${request.email}")
        
        try {
            val response = authService.authenticate(request)
            logger.info("Usuário autenticado com sucesso: ${request.email}")
            return ResponseEntity.ok(response)
        } catch (e: Exception) {
            logger.error("Erro ao autenticar usuário: ${e.message}", e)
            throw e
        }
    }
} 