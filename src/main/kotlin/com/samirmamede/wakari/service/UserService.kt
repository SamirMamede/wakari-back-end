package com.samirmamede.wakari.service

import com.samirmamede.wakari.dto.UserResponse
import com.samirmamede.wakari.exception.ResourceNotFoundException
import com.samirmamede.wakari.model.User
import com.samirmamede.wakari.model.UserRole
import com.samirmamede.wakari.repository.UserRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
class UserService(
    private val userRepository: UserRepository
) {
    /**
     * Busca todos os usuários com paginação
     */
    fun findAll(pageable: Pageable): Page<User> =
        userRepository.findAll(pageable)
    
    /**
     * Busca um usuário pelo ID
     */
    fun findById(id: Long): User =
        userRepository.findById(id)
            .orElseThrow { ResourceNotFoundException("Usuário não encontrado com id: $id") }
    
    /**
     * Busca um usuário pelo email
     */
    fun findByEmail(email: String): User =
        userRepository.findByEmail(email)
            .orElseThrow { ResourceNotFoundException("Usuário não encontrado com email: $email") }
    
    /**
     * Atualiza o nome do usuário
     */
    @Transactional
    fun updateName(id: Long, name: String): User {
        val user = findById(id)
        user.name = name
        return userRepository.save(user)
    }
    
    /**
     * Atualiza o perfil do usuário
     */
    @Transactional
    fun updateRole(id: Long, role: UserRole): User {
        val user = findById(id)
        user.role = role
        return userRepository.save(user)
    }
    
    /**
     * Remove um usuário
     */
    @Transactional
    fun delete(id: Long) {
        if (!userRepository.existsById(id)) {
            throw ResourceNotFoundException("Usuário não encontrado com id: $id")
        }
        userRepository.deleteById(id)
    }
} 