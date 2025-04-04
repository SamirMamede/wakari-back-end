package com.samirmamede.wakari.dto

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

@Schema(description = "Dados para autenticação de um usuário")
data class AuthRequest(
    @field:NotBlank(message = "Email é obrigatório")
    @field:Email(message = "Email inválido")
    @field:Schema(description = "Email do usuário", example = "joao.silva@email.com", required = true)
    val email: String,
    
    @field:NotBlank(message = "Senha é obrigatória")
    @field:Size(min = 6, message = "Senha deve ter pelo menos 6 caracteres")
    @field:Schema(description = "Senha do usuário", example = "senha123", required = true)
    val password: String
) 