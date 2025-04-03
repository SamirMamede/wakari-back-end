package com.samirmamede.wakari.repository

import com.samirmamede.wakari.model.StockItem
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface StockItemRepository : JpaRepository<StockItem, Long> {
    fun findByNameContainingIgnoreCase(name: String): List<StockItem>
    
    @Query("SELECT s FROM StockItem s WHERE s.quantity <= s.minQuantity")
    fun findAllLowStock(): List<StockItem>
} 