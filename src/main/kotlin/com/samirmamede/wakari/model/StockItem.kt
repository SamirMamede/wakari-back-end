package com.samirmamede.wakari.model

import jakarta.persistence.*
import java.math.BigDecimal
import java.time.LocalDateTime

@Entity
@Table(name = "stock_items")
data class StockItem(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    
    @Column(nullable = false)
    var name: String,
    
    @Column(nullable = false)
    var quantity: BigDecimal = BigDecimal.ZERO,
    
    @Column(nullable = false)
    var unit: String,
    
    @Column(name = "min_quantity", nullable = false)
    var minQuantity: BigDecimal = BigDecimal.ZERO,
    
    @OneToMany(mappedBy = "stockItem")
    val recipes: MutableList<Recipe> = mutableListOf(),
    
    @Column(name = "created_at", nullable = false, updatable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),
    
    @Column(name = "updated_at", nullable = false)
    var updatedAt: LocalDateTime = LocalDateTime.now()
) {
    fun isLowStock(): Boolean {
        return quantity <= minQuantity
    }
    constructor(id: Long) : this(
        id = id,
        name = "Placeholder",
        unit = "un"
    )
} 