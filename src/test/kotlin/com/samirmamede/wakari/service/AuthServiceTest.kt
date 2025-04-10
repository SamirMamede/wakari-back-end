package com.samirmamede.wakari.service

import com.samirmamede.wakari.dto.AuthRequest
import com.samirmamede.wakari.dto.RegisterRequest
import com.samirmamede.wakari.exception.UserAlreadyExistsException
import com.samirmamede.wakari.model.User
import com.samirmamede.wakari.model.UserRole
import com.samirmamede.wakari.repository.UserRepository
import com.samirmamede.wakari.security.JwtService
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.ArgumentMatchers
import org.mockito.Mockito.*
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.password.PasswordEncoder
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import org.springframework.security.authentication.BadCredentialsException

class AuthServiceTest {

    private lateinit var authService: AuthService
    private lateinit var userRepository: UserRepository
    private lateinit var passwordEncoder: PasswordEncoder
    private lateinit var jwtService: JwtService
    private lateinit var authenticationManager: AuthenticationManager

    @BeforeEach
    fun setup() {
        userRepository = mock(UserRepository::class.java)
        passwordEncoder = mock(PasswordEncoder::class.java)
        jwtService = mock(JwtService::class.java)
        authenticationManager = mock(AuthenticationManager::class.java)

        authService = AuthService(
            userRepository = userRepository,
            passwordEncoder = passwordEncoder,
            jwtService = jwtService,
            authenticationManager = authenticationManager
        )
    }

    @Test
    fun `register should create user and return token`() {
        // Arrange
        val request = RegisterRequest(
            name = "Test User",
            email = "test@example.com",
            password = "password123",
            role = UserRole.ADMIN
        )
        
        val encodedPassword = "encodedPassword"
        val userId = 1L
        val token = "jwt.token.here"
        
        `when`(userRepository.existsByEmail(request.email)).thenReturn(false)
        `when`(passwordEncoder.encode(request.password)).thenReturn(encodedPassword)
        
        val savedUser = User(
            id = userId,
            name = request.name,
            email = request.email,
            password = encodedPassword,
            role = request.role
        )
        
        `when`(userRepository.save(any())).thenReturn(savedUser)
        `when`(jwtService.generateToken(any(), any())).thenReturn(token)
        
        // Act
        val result = authService.register(request)
        
        // Assert
        assertNotNull(result)
        assertEquals(token, result.token)
        assertEquals(userId, result.userId)
        assertEquals(request.name, result.name)
        assertEquals(request.email, result.email)
        assertEquals(request.role.name, result.role)
        
        verify(userRepository).existsByEmail(request.email)
        verify(passwordEncoder).encode(request.password)
        verify(userRepository).save(any())
        verify(jwtService).generateToken(any(), any())
    }
    
    @Test
    fun `register should throw exception when email already exists`() {
        // Arrange
        val request = RegisterRequest(
            name = "Test User",
            email = "existing@example.com",
            password = "password123",
            role = UserRole.ADMIN
        )
        
        `when`(userRepository.existsByEmail(request.email)).thenReturn(true)
        
        // Act & Assert
        assertThrows<UserAlreadyExistsException> { 
            authService.register(request) 
        }
        
        verify(userRepository).existsByEmail(request.email)
        verifyNoMoreInteractions(passwordEncoder, jwtService)
        verify(userRepository, never()).save(any())
    }
    
    @Test
    fun `authenticate should return token for valid credentials`() {
        // Arrange
        val request = AuthRequest(
            email = "test@example.com",
            password = "password123"
        )
        
        val userId = 1L
        val userName = "Test User"
        val userRole = UserRole.ADMIN
        val token = "jwt.token.here"
        
        val user = User(
            id = userId,
            name = userName,
            email = request.email,
            password = "encodedPassword",
            role = userRole
        )
        
        `when`(userRepository.findByEmail(request.email)).thenReturn(Optional.of(user))
        `when`(jwtService.generateToken(any(), any())).thenReturn(token)
        
        // Act
        val result = authService.authenticate(request)
        
        // Assert
        assertNotNull(result)
        assertEquals(token, result.token)
        assertEquals(userId, result.userId)
        assertEquals(userName, result.name)
        assertEquals(request.email, result.email)
        assertEquals(userRole.name, result.role)
        
        verify(authenticationManager).authenticate(any())
        verify(userRepository).findByEmail(request.email)
        verify(jwtService).generateToken(any(), any())
    }
    
    @Test
    fun `authenticate should throw exception when user not found`() {
        // Arrange
        val request = AuthRequest(
            email = "nonexistent@example.com",
            password = "password123"
        )
        
        `when`(userRepository.findByEmail(request.email)).thenReturn(Optional.empty())
        
        // Act & Assert
        assertThrows<UsernameNotFoundException> { 
            authService.authenticate(request) 
        }
        
        verify(authenticationManager).authenticate(any())
        verify(userRepository).findByEmail(request.email)
        verifyNoMoreInteractions(jwtService)
    }
    
    @Test
    fun `authenticate should throw exception for invalid password`() {
        // Arrange
        val request = AuthRequest(
            email = "test@example.com",
            password = "wrong_password"
        )
        
        `when`(authenticationManager.authenticate(any()))
            .thenThrow(BadCredentialsException("Invalid credentials"))
        
        // Act & Assert
        assertThrows<BadCredentialsException> {
            authService.authenticate(request)
        }
    }
    
    // Função auxiliar para ajudar o Mockito com a tipagem
    private fun <T> any(): T {
        return ArgumentMatchers.any()
    }
} 