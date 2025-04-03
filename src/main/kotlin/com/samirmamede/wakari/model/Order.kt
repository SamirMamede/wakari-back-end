package com.samirmamede.wakari.model

import jakarta.persistence.*
import java.math.BigDecimal
import java.time.LocalDateTime

@Entity
@Table(name = "orders")
data class Order(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    val user: User,
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "table_id")
    val table: RestaurantTable? = null,
    
    @Column(nullable = false)
    var total: BigDecimal = BigDecimal.ZERO,
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var status: OrderStatus = OrderStatus.PENDING,
    
    @Column(name = "is_delivery", nullable = false)
    var isDelivery: Boolean = false,
    
    @OneToMany(mappedBy = "order", cascade = [CascadeType.ALL], orphanRemoval = true)
    val items: MutableList<OrderItem> = mutableListOf(),
    
    @Column(name = "created_at", nullable = false, updatable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),
    
    @Column(name = "updated_at", nullable = false)
    var updatedAt: LocalDateTime = LocalDateTime.now()
) {
    fun calculateTotal() {
        total = items.sumOf { it.quantity.toBigDecimal() * it.priceAtTime }
    }
}

enum class OrderStatus {
    PENDING, PREPARING, READY, DELIVERED, CANCELED
} 