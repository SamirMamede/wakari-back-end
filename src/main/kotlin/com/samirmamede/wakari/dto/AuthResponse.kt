package com.samirmamede.wakari.dto

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "Resposta da autenticação/registro com token JWT e dados do usuário")
data class AuthResponse(
    @field:Schema(description = "Token JWT para autenticação nas requisições subsequentes", 
                example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    val token: String,
    
    @field:Schema(description = "ID do usuário no sistema", example = "1")
    val userId: Long,
    
    @field:Schema(description = "Nome do usuário", example = "João Silva")
    val name: String,
    
    @field:Schema(description = "Email do usuário", example = "joao.silva@email.com")
    val email: String,
    
    @field:Schema(description = "Perfil do usuário no sistema", example = "ADMIN", 
               allowableValues = ["ADMIN", "GARCOM", "COZINHA", "CLIENTE", "ENTREGADOR"])
    val role: String
) 