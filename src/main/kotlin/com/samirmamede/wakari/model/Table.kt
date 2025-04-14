package com.samirmamede.wakari.model

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "tables")
data class RestaurantTable(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    
    @Column(nullable = false, unique = true)
    val number: Int,
    
    @Column(nullable = false)
    val capacity: Int,
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var status: TableStatus = TableStatus.AVAILABLE,
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "current_order_id")
    var currentOrder: Order? = null,
    
    @Column(name = "created_at", nullable = false, updatable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),
    
    @Column(name = "updated_at", nullable = false)
    var updatedAt: LocalDateTime = LocalDateTime.now()
)

enum class TableStatus {
    AVAILABLE, OCCUPIED, RESERVED, CLEANING
} 