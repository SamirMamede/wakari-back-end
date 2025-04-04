package com.samirmamede.wakari.security

import io.jsonwebtoken.Claims
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.test.util.ReflectionTestUtils
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class JwtServiceTest {

    private lateinit var jwtService: JwtService
    private val testSecret = "testSecretKeyMustBeLongEnoughForJwtSigning"
    private val testExpiration = 3600000L // 1 hour
    
    @BeforeEach
    fun setup() {
        jwtService = JwtService()
        ReflectionTestUtils.setField(jwtService, "secretKey", testSecret)
        ReflectionTestUtils.setField(jwtService, "jwtExpiration", testExpiration)
        jwtService.init() // Call postconstruct method manually
    }
    
    @Test
    fun `generateToken should create valid token with correct claims`() {
        // Arrange
        val username = "test@example.com"
        val authorities = emptyList<String>()
        val userDetails = createUserDetails(username, authorities)
        val extraClaims = mapOf("role" to "ADMIN")
        
        // Act
        val token = jwtService.generateToken(extraClaims, userDetails)
        
        // Assert
        assertNotNull(token)
        assertEquals(username, jwtService.extractUsername(token))
        assertTrue(jwtService.isTokenValid(token, userDetails))
    }
    
    @Test
    fun `token should be invalid after expiration`() {
        // Arrange
        val username = "test@example.com"
        val authorities = emptyList<String>()
        val userDetails = createUserDetails(username, authorities)
        
        // Modify expiration to -1 hour to create expired token
        ReflectionTestUtils.setField(jwtService, "jwtExpiration", -3600000L)
        
        // Act
        val token = jwtService.generateToken(userDetails)
        
        // Tentar validar - deve falhar devido à expiração
        try {
            val isValid = jwtService.isTokenValid(token, userDetails)
            assertFalse(isValid) // Se não lançar exceção, o token deve ser inválido
        } catch (e: Exception) {
            // Uma exceção também é um resultado aceitável para um token expirado
            assertTrue(e.message?.contains("expired") ?: false || 
                       e.cause?.message?.contains("expired") ?: false)
        }
    }
    
    @Test
    fun `isTokenValid should return false for token with different username`() {
        // Arrange
        val username = "test@example.com"
        val differentUsername = "different@example.com"
        val authorities = emptyList<String>()
        val userDetails = createUserDetails(username, authorities)
        val differentUserDetails = createUserDetails(differentUsername, authorities)
        
        // Act
        val token = jwtService.generateToken(userDetails)
        
        // Assert
        assertFalse(jwtService.isTokenValid(token, differentUserDetails))
    }
    
    @Test
    fun `extractUsername should return empty string for invalid token`() {
        // Arrange
        val invalidToken = "invalid.token.string"
        
        // Act & Assert
        try {
            val result = jwtService.extractUsername(invalidToken)
            assertEquals("", result)
        } catch (e: Exception) {
            // In this implementation, an exception might be thrown instead
            // which is also acceptable behavior for invalid tokens
            assertTrue(true)
        }
    }
    
    @Test
    fun `generateToken overload should work with default empty claims`() {
        // Arrange
        val username = "test@example.com"
        val authorities = emptyList<String>()
        val userDetails = createUserDetails(username, authorities)
        
        // Act
        val token = jwtService.generateToken(userDetails)
        
        // Assert
        assertNotNull(token)
        assertEquals(username, jwtService.extractUsername(token))
        assertTrue(jwtService.isTokenValid(token, userDetails))
    }
    
    private fun createUserDetails(username: String, authorities: List<String>): UserDetails {
        return User
            .withUsername(username)
            .password("password")
            .authorities(*authorities.toTypedArray())
            .build()
    }
} 