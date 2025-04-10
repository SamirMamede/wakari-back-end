package com.samirmamede.wakari.model

import jakarta.persistence.*
import java.math.BigDecimal
import java.time.LocalDateTime

@Entity
@Table(name = "menu_items")
data class MenuItem(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    
    @Column(nullable = false)
    var name: String,
    
    @Column
    var description: String? = null,
    
    @Column(nullable = false)
    var price: BigDecimal,
    
    @Column(nullable = false)
    var available: Boolean = true,
    
    @Column(name = "image_url")
    var imageUrl: String? = null,
    
    @Column(nullable = false)
    var category: String,
    
    @OneToMany(mappedBy = "menuItem", cascade = [CascadeType.ALL], orphanRemoval = true)
    val recipes: MutableList<Recipe> = mutableListOf(),
    
    @Column(name = "created_at", nullable = false, updatable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),
    
    @Column(name = "updated_at", nullable = false)
    var updatedAt: LocalDateTime = LocalDateTime.now()
) {

    constructor(id: Long) : this(
        id = id,
        name = "Placeholder",
        price = BigDecimal.ZERO,
        category = "Placeholder"
    )
} 