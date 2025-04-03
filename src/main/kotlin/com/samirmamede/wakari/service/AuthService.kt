package com.samirmamede.wakari.service

import com.samirmamede.wakari.dto.AuthRequest
import com.samirmamede.wakari.dto.AuthResponse
import com.samirmamede.wakari.dto.RegisterRequest
import com.samirmamede.wakari.exception.UserAlreadyExistsException
import com.samirmamede.wakari.model.User
import com.samirmamede.wakari.repository.UserRepository
import com.samirmamede.wakari.security.JwtService
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
class AuthService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtService: JwtService,
    private val authenticationManager: AuthenticationManager
) {

    @Transactional
    fun register(request: RegisterRequest): AuthResponse {
        if (userRepository.existsByEmail(request.email)) {
            throw UserAlreadyExistsException("Email já cadastrado: ${request.email}")
        }
        
        val user = User(
            name = request.name,
            email = request.email,
            password = passwordEncoder.encode(request.password),
            role = request.role
        )
        
        val savedUser = userRepository.save(user)
        
        val userDetails = org.springframework.security.core.userdetails.User
            .withUsername(savedUser.email)
            .password(savedUser.password)
            .authorities("ROLE_${savedUser.role.name}")
            .build()
            
        val token = jwtService.generateToken(
            mapOf("role" to savedUser.role.name),
            userDetails
        )
        
        return AuthResponse(
            token = token,
            userId = savedUser.id,
            name = savedUser.name,
            email = savedUser.email,
            role = savedUser.role.name
        )
    }
    
    fun authenticate(request: AuthRequest): AuthResponse {
        authenticationManager.authenticate(
            UsernamePasswordAuthenticationToken(
                request.email,
                request.password
            )
        )
        
        val user = userRepository.findByEmail(request.email)
            .orElseThrow { UsernameNotFoundException("Usuário não encontrado") }
        
        val userDetails = org.springframework.security.core.userdetails.User
            .withUsername(user.email)
            .password(user.password)
            .authorities("ROLE_${user.role.name}")
            .build()
            
        val token = jwtService.generateToken(
            mapOf("role" to user.role.name),
            userDetails
        )
        
        return AuthResponse(
            token = token,
            userId = user.id,
            name = user.name,
            email = user.email,
            role = user.role.name
        )
    }
} 