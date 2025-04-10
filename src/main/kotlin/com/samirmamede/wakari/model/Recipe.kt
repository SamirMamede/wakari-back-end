package com.samirmamede.wakari.model

import jakarta.persistence.*
import java.math.BigDecimal
import java.time.LocalDateTime

@Entity
@Table(name = "recipes")
data class Recipe(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menu_item_id", nullable = false)
    val menuItem: MenuItem,
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stock_item_id", nullable = false)
    val stockItem: StockItem,
    
    @Column(nullable = false)
    var quantity: BigDecimal,

    @Column(nullable = false)
    var cost: BigDecimal,
    
    @Column(name = "created_at", nullable = false, updatable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),
    
    @Column(name = "updated_at", nullable = false)
    var updatedAt: LocalDateTime = LocalDateTime.now()
) {
    constructor(menuItem: MenuItem, stockItem: StockItem, quantity: BigDecimal, cost: BigDecimal) : this(
        id = 0,
        menuItem = menuItem,
        stockItem = stockItem,
        quantity = quantity,
        cost = cost
    )

    fun copy(quantity: BigDecimal? = null, cost: BigDecimal? = null): Recipe {
        return Recipe(
            id = this.id,
            menuItem = this.menuItem,
            stockItem = this.stockItem,
            quantity = quantity ?: this.quantity,
            cost = cost ?: this.cost,
            createdAt = this.createdAt,
            updatedAt = LocalDateTime.now()
        )
    }
} 