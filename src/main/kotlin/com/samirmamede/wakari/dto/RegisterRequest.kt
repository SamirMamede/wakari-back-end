package com.samirmamede.wakari.dto

import com.samirmamede.wakari.model.UserRole
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size

@Schema(description = "Dados para registro de um novo usuário")
data class RegisterRequest(
    @field:NotBlank(message = "Nome é obrigatório")
    @field:Schema(description = "Nome completo do usuário", example = "João Silva", required = true)
    val name: String,
    
    @field:NotBlank(message = "Email é obrigatório")
    @field:Email(message = "Email inválido")
    @field:Schema(description = "Email do usuário (usado para login)", example = "joao.silva@email.com", required = true)
    val email: String,
    
    @field:NotBlank(message = "Senha é obrigatória")
    @field:Size(min = 6, message = "Senha deve ter pelo menos 6 caracteres")
    @field:Schema(description = "Senha do usuário (mínimo 6 caracteres)", example = "senha123", required = true)
    val password: String,
    
    @field:NotNull(message = "Perfil é obrigatório")
    @field:Schema(description = "Perfil do usuário no sistema", example = "ADMIN", required = true, 
                 allowableValues = ["ADMIN", "GARCOM", "COZINHA", "CLIENTE", "ENTREGADOR"])
    val role: UserRole
) 