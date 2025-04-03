package com.samirmamede.wakari.dto

import com.samirmamede.wakari.model.UserRole
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size

data class RegisterRequest(
    @field:NotBlank(message = "Nome é obrigatório")
    val name: String,
    
    @field:NotBlank(message = "Email é obrigatório")
    @field:Email(message = "Email inválido")
    val email: String,
    
    @field:NotBlank(message = "Senha é obrigatória")
    @field:Size(min = 6, message = "Senha deve ter pelo menos 6 caracteres")
    val password: String,
    
    @field:NotNull(message = "Perfil é obrigatório")
    val role: UserRole
) 