package com.samirmamede.wakari.security

import com.samirmamede.wakari.repository.UserRepository
import org.slf4j.LoggerFactory
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserDetailsServiceImpl(private val userRepository: UserRepository) : UserDetailsService {
    
    private val logger = LoggerFactory.getLogger(UserDetailsServiceImpl::class.java)

    @Transactional(readOnly = true)
    override fun loadUserByUsername(username: String): UserDetails {
        logger.debug("Buscando usuário pelo email: $username")
        
        val user = userRepository.findByEmail(username)
            .orElseThrow { 
                logger.warn("Usuário não encontrado com o email: $username")
                UsernameNotFoundException("Usuário não encontrado com o email: $username") 
            }
        
        logger.debug("Usuário encontrado: ${user.name}, com perfil: ${user.role}")
        
        // Criar uma autoridade baseada no role do usuário
        val authorities = listOf(SimpleGrantedAuthority("ROLE_${user.role.name}"))
        
        // Retornar um objeto UserDetails com as informações necessárias
        return User.builder()
            .username(user.email)
            .password(user.password)
            .authorities(authorities)
            .accountExpired(false)
            .accountLocked(false)
            .credentialsExpired(false)
            .disabled(false)
            .build()
    }
} 