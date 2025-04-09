package com.samirmamede.wakari.service

import com.samirmamede.wakari.model.StockItem
import com.samirmamede.wakari.repository.StockItemRepository
import jakarta.persistence.EntityNotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.time.LocalDateTime

@Service
class StockItemService(private val stockItemRepository: StockItemRepository) {
    
    fun findAll(): List<StockItem> = stockItemRepository.findAll()
    
    fun findById(id: Long): StockItem = stockItemRepository.findById(id)
        .orElseThrow { EntityNotFoundException("Item de estoque não encontrado com ID: $id") }
    
    fun findByName(name: String): List<StockItem> = stockItemRepository.findByNameContainingIgnoreCase(name)
    
    fun findAllLowStock(): List<StockItem> = stockItemRepository.findAllLowStock()
    
    @Transactional
    fun create(stockItem: StockItem): StockItem = stockItemRepository.save(stockItem)
    
    @Transactional
    fun update(id: Long, updatedItem: StockItem): StockItem {
        val existingItem = findById(id)
        
        existingItem.name = updatedItem.name
        existingItem.quantity = updatedItem.quantity
        existingItem.unit = updatedItem.unit
        existingItem.minQuantity = updatedItem.minQuantity
        existingItem.updatedAt = LocalDateTime.now()
        
        return stockItemRepository.save(existingItem)
    }
    
    @Transactional
    fun updateQuantity(id: Long, quantity: BigDecimal): StockItem {
        val item = findById(id)
        item.quantity = quantity
        item.updatedAt = LocalDateTime.now()
        return stockItemRepository.save(item)
    }
    
    @Transactional
    fun addToStock(id: Long, amount: BigDecimal): StockItem {
        val item = findById(id)
        item.quantity = item.quantity.add(amount)
        item.updatedAt = LocalDateTime.now()
        return stockItemRepository.save(item)
    }
    
    @Transactional
    fun removeFromStock(id: Long, amount: BigDecimal): StockItem {
        val item = findById(id)
        if (item.quantity < amount) {
            throw IllegalArgumentException("Quantidade insuficiente em estoque para o item: ${item.name}")
        }
        item.quantity = item.quantity.subtract(amount)
        item.updatedAt = LocalDateTime.now()
        return stockItemRepository.save(item)
    }
    
    @Transactional
    fun delete(id: Long) {
        val item = findById(id)
        if (item.recipes.isNotEmpty()) {
            throw IllegalStateException("Não é possível excluir item de estoque que está sendo usado em receitas")
        }
        stockItemRepository.deleteById(id)
    }
} 