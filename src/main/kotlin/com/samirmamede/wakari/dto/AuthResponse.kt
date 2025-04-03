package com.samirmamede.wakari.dto

data class AuthResponse(
    val token: String,
    val userId: Long,
    val name: String,
    val email: String,
    val role: String
) 