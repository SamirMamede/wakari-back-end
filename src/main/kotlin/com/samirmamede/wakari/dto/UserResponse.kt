package com.samirmamede.wakari.dto

import com.samirmamede.wakari.model.User
import com.samirmamede.wakari.model.UserRole
import java.time.LocalDateTime

data class UserResponse(
    val id: Long,
    val name: String,
    val email: String,
    val role: UserRole,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime?
) {
    companion object {
        fun fromEntity(user: User): UserResponse {
            return UserResponse(
                id = user.id,
                name = user.name,
                email = user.email,
                role = user.role,
                createdAt = user.createdAt,
                updatedAt = user.updatedAt
            )
        }
    }
} 