package com.samirmamede.wakari.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.samirmamede.wakari.dto.AuthRequest
import com.samirmamede.wakari.dto.AuthResponse
import com.samirmamede.wakari.dto.RegisterRequest
import com.samirmamede.wakari.exception.UserAlreadyExistsException
import com.samirmamede.wakari.model.UserRole
import com.samirmamede.wakari.service.AuthService
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.springframework.http.MediaType
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.MockMvcBuilders

class AuthControllerTest {

    private lateinit var mockMvc: MockMvc
    private lateinit var objectMapper: ObjectMapper

    @Mock
    private lateinit var authService: AuthService

    @InjectMocks
    private lateinit var authController: AuthController

    @BeforeEach
    fun setup() {
        MockitoAnnotations.openMocks(this)
        objectMapper = ObjectMapper()
        mockMvc = MockMvcBuilders.standaloneSetup(authController)
            .setControllerAdvice(com.samirmamede.wakari.exception.GlobalExceptionHandler())
            .build()
    }

    @Test
    fun `register should return 201 with token when successful`() {
        // Arrange
        val request = RegisterRequest(
            name = "Test User",
            email = "test@example.com",
            password = "password123",
            role = UserRole.ADMIN
        )

        val response = AuthResponse(
            token = "jwt.token.here",
            userId = 1L,
            name = request.name,
            email = request.email,
            role = request.role.name
        )

        `when`(authService.register(request)).thenReturn(response)

        // Act & Assert
        mockMvc.perform(
            post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.token").value(response.token))
            .andExpect(jsonPath("$.userId").value(response.userId))
            .andExpect(jsonPath("$.name").value(response.name))
            .andExpect(jsonPath("$.email").value(response.email))
            .andExpect(jsonPath("$.role").value(response.role))
    }

    @Test
    fun `register should return 409 when email already exists`() {
        // Arrange
        val request = RegisterRequest(
            name = "Test User",
            email = "existing@example.com",
            password = "password123",
            role = UserRole.ADMIN
        )

        `when`(authService.register(request)).thenThrow(UserAlreadyExistsException("Email já cadastrado"))

        // Act & Assert
        mockMvc.perform(
            post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isConflict)
    }

    @Test
    fun `authenticate should return 200 with token when credentials are valid`() {
        // Arrange
        val request = AuthRequest(
            email = "test@example.com",
            password = "password123"
        )

        val response = AuthResponse(
            token = "jwt.token.here",
            userId = 1L,
            name = "Test User",
            email = request.email,
            role = "ADMIN"
        )

        `when`(authService.authenticate(request)).thenReturn(response)

        // Act & Assert
        mockMvc.perform(
            post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.token").value(response.token))
            .andExpect(jsonPath("$.userId").value(response.userId))
            .andExpect(jsonPath("$.name").value(response.name))
            .andExpect(jsonPath("$.email").value(response.email))
            .andExpect(jsonPath("$.role").value(response.role))
    }

    @Test
    fun `authenticate should return 401 when credentials are invalid`() {
        // Arrange
        val request = AuthRequest(
            email = "test@example.com",
            password = "wrongpassword"
        )

        `when`(authService.authenticate(request)).thenThrow(BadCredentialsException("Credenciais inválidas"))

        // Act & Assert
        mockMvc.perform(
            post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isUnauthorized)
    }
} 