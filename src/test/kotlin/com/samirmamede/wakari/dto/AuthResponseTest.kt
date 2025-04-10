package com.samirmamede.wakari.dto

import com.samirmamede.wakari.model.UserRole
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class AuthResponseTest {
    
    @Test
    fun `should create response with valid data`() {
        val token = "test.jwt.token"
        val userId = 1L
        val name = "Test User"
        val email = "test@example.com"
        val role = UserRole.ADMIN.name
        
        val response = AuthResponse(
            token = token,
            userId = userId,
            name = name,
            email = email,
            role = role
        )
        
        assertNotNull(response)
        assertEquals(token, response.token)
        assertEquals(userId, response.userId)
        assertEquals(name, response.name)
        assertEquals(email, response.email)
        assertEquals(role, response.role)
    }
    
    @Test
    fun `should accept all user role types`() {
        UserRole.values().forEach { userRole ->
            val response = AuthResponse(
                token = "test.jwt.token",
                userId = 1L,
                name = "Test User",
                email = "test@example.com",
                role = userRole.name
            )
            
            assertEquals(userRole.name, response.role)
        }
    }
    
    @Test
    fun `should create copy with updated fields`() {
        val original = AuthResponse(
            token = "test.jwt.token",
            userId = 1L,
            name = "Test User",
            email = "test@example.com",
            role = UserRole.ADMIN.name
        )
        
        val copy = original.copy(
            token = "new.jwt.token",
            name = "New User"
        )
        
        assertEquals("new.jwt.token", copy.token)
        assertEquals("New User", copy.name)
        assertEquals(original.userId, copy.userId)
        assertEquals(original.email, copy.email)
        assertEquals(original.role, copy.role)
    }
    
    @Test
    fun `should implement equals and hashCode correctly`() {
        val response1 = AuthResponse(
            token = "test.jwt.token",
            userId = 1L,
            name = "Test User",
            email = "test@example.com",
            role = UserRole.ADMIN.name
        )
        
        val response2 = AuthResponse(
            token = "test.jwt.token",
            userId = 1L,
            name = "Test User",
            email = "test@example.com",
            role = UserRole.ADMIN.name
        )
        
        val response3 = AuthResponse(
            token = "different.token",
            userId = 1L,
            name = "Test User",
            email = "test@example.com",
            role = UserRole.ADMIN.name
        )
        
        assertEquals(response1, response2)
        assertEquals(response1.hashCode(), response2.hashCode())
        assert(response1 != response3)
    }
} 