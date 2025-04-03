package com.samirmamede.wakari.model

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "users")
data class User(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    
    @Column(nullable = false)
    var name: String,
    
    @Column(nullable = false, unique = true)
    var email: String,
    
    @Column(nullable = false)
    var password: String,
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var role: UserRole,
    
    @Column(name = "created_at", nullable = false, updatable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),
    
    @Column(name = "updated_at", nullable = false)
    var updatedAt: LocalDateTime = LocalDateTime.now()
)

enum class UserRole {
    ADMIN, WAITER, KITCHEN, CUSTOMER, DELIVERY
} 