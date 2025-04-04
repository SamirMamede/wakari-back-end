package com.samirmamede.wakari.integration

import com.fasterxml.jackson.databind.ObjectMapper
import com.samirmamede.wakari.dto.AuthRequest
import com.samirmamede.wakari.dto.RegisterRequest
import com.samirmamede.wakari.model.UserRole
import com.samirmamede.wakari.repository.UserRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Disabled("Testes de integração serão habilitados quando o ambiente estiver totalmente configurado")
class AuthIntegrationTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @Autowired
    private lateinit var userRepository: UserRepository

    @BeforeEach
    fun setup() {
        // Limpar a base de dados antes de cada teste
        userRepository.deleteAll()
    }

    @Test
    fun `register and authenticate flow should work`() {
        // 1. Registrar um novo usuário
        val registerRequest = RegisterRequest(
            name = "Test User",
            email = "integration-test@example.com",
            password = "integration-password",
            role = UserRole.ADMIN
        )

        mockMvc.perform(
            post("/auth/register")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest))
        )
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.token").exists())
            .andExpect(jsonPath("$.userId").exists())
            .andExpect(jsonPath("$.name").value(registerRequest.name))
            .andExpect(jsonPath("$.email").value(registerRequest.email))
            .andExpect(jsonPath("$.role").value(registerRequest.role.name))

        // 2. Autenticar o usuário registrado
        val authRequest = AuthRequest(
            email = registerRequest.email,
            password = registerRequest.password
        )

        mockMvc.perform(
            post("/auth/login")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(authRequest))
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.token").exists())
            .andExpect(jsonPath("$.userId").exists())
            .andExpect(jsonPath("$.name").value(registerRequest.name))
            .andExpect(jsonPath("$.email").value(registerRequest.email))
            .andExpect(jsonPath("$.role").value(registerRequest.role.name))
    }

    @Test
    fun `register should fail when email already exists`() {
        // 1. Registrar um usuário
        val registerRequest = RegisterRequest(
            name = "Duplicate User",
            email = "duplicate@example.com",
            password = "duplicate-password",
            role = UserRole.ADMIN
        )

        mockMvc.perform(
            post("/auth/register")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest))
        )
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.token").exists())

        // 2. Tentar registrar outro usuário com o mesmo email
        mockMvc.perform(
            post("/auth/register")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest))
        )
            .andExpect(status().isConflict)
    }

    @Test
    fun `login should fail with invalid credentials`() {
        // 1. Registrar um usuário
        val registerRequest = RegisterRequest(
            name = "Invalid Credentials User",
            email = "invalid-creds@example.com",
            password = "valid-password",
            role = UserRole.ADMIN
        )

        mockMvc.perform(
            post("/auth/register")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest))
        )
            .andExpect(status().isCreated)

        // 2. Tentar fazer login com senha errada
        val authRequest = AuthRequest(
            email = registerRequest.email,
            password = "wrong-password"
        )

        mockMvc.perform(
            post("/auth/login")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(authRequest))
        )
            .andExpect(status().isUnauthorized)
    }
} 