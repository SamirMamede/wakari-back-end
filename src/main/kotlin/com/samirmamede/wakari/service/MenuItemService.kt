package com.samirmamede.wakari.service

import com.samirmamede.wakari.model.MenuItem
import com.samirmamede.wakari.model.Recipe
import com.samirmamede.wakari.repository.MenuItemRepository
import com.samirmamede.wakari.repository.RecipeRepository
import com.samirmamede.wakari.repository.StockItemRepository
import jakarta.persistence.EntityNotFoundException
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
class MenuItemService(
    private val menuItemRepository: MenuItemRepository,
    private val recipeRepository: RecipeRepository,
    private val stockItemRepository: StockItemRepository
) {
    fun findAll(pageable: Pageable): Page<MenuItem> = menuItemRepository.findAll(pageable)
    
    fun findById(id: Long): MenuItem = menuItemRepository.findById(id)
        .orElseThrow { EntityNotFoundException("Item do cardápio não encontrado com ID: $id") }
    
    fun findByAvailable(available: Boolean, pageable: Pageable): Page<MenuItem> = 
        menuItemRepository.findByAvailable(available, pageable)
    
    fun findByCategory(category: String, pageable: Pageable): Page<MenuItem> = 
        menuItemRepository.findByCategory(category, pageable)
    
    fun findAllCategories(): List<String> = menuItemRepository.findAllCategories()
    
    @Transactional
    fun create(menuItem: MenuItem): MenuItem {
        val savedMenuItem = menuItemRepository.save(menuItem)
        
        // Verificar disponibilidade com base nos ingredientes
        updateAvailabilityBasedOnStock(savedMenuItem)
        
        return savedMenuItem
    }
    
    @Transactional
    fun update(id: Long, updatedItem: MenuItem): MenuItem {
        val existingItem = findById(id)
        
        existingItem.name = updatedItem.name
        existingItem.description = updatedItem.description
        existingItem.price = updatedItem.price
        existingItem.available = updatedItem.available
        existingItem.imageUrl = updatedItem.imageUrl
        existingItem.category = updatedItem.category
        existingItem.updatedAt = LocalDateTime.now()
        
        val savedItem = menuItemRepository.save(existingItem)
        
        // Verificar disponibilidade com base nos ingredientes
        updateAvailabilityBasedOnStock(savedItem)
        
        return savedItem
    }
    
    @Transactional
    fun addRecipe(menuItemId: Long, recipe: Recipe): MenuItem {
        val menuItem = findById(menuItemId)
        
        // Verificar se o item de estoque existe
        val stockItem = stockItemRepository.findById(recipe.stockItem.id)
            .orElseThrow { EntityNotFoundException("Item de estoque não encontrado com ID: ${recipe.stockItem.id}") }
        
        // Verificar se já existe uma receita para este item do cardápio e item de estoque
        val existingRecipe = recipeRepository.findByMenuItemIdAndStockItemId(menuItemId, stockItem.id)
        if (existingRecipe != null) {
            throw IllegalStateException("Já existe uma receita para este item do cardápio com este ingrediente")
        }
        
        // Criar a nova receita
        val newRecipe = Recipe(
            menuItem = menuItem,
            stockItem = stockItem,
            quantity = recipe.quantity
        )
        
        recipeRepository.save(newRecipe)
        
        // Atualizar disponibilidade do item do cardápio
        updateAvailabilityBasedOnStock(menuItem)
        
        return menuItem
    }
    
    @Transactional
    fun updateRecipe(menuItemId: Long, recipeId: Long, updatedQuantity: java.math.BigDecimal): Recipe {
        val recipe = recipeRepository.findById(recipeId)
            .orElseThrow { EntityNotFoundException("Receita não encontrada com ID: $recipeId") }
        
        if (recipe.menuItem.id != menuItemId) {
            throw IllegalArgumentException("A receita não pertence ao item do cardápio especificado")
        }
        
        recipe.quantity = updatedQuantity
        
        val updatedRecipe = recipeRepository.save(recipe)
        
        // Atualizar disponibilidade do item do cardápio
        updateAvailabilityBasedOnStock(recipe.menuItem)
        
        return updatedRecipe
    }
    
    @Transactional
    fun removeRecipe(menuItemId: Long, recipeId: Long) {
        val recipe = recipeRepository.findById(recipeId)
            .orElseThrow { EntityNotFoundException("Receita não encontrada com ID: $recipeId") }
        
        if (recipe.menuItem.id != menuItemId) {
            throw IllegalArgumentException("A receita não pertence ao item do cardápio especificado")
        }
        
        recipeRepository.delete(recipe)
        
        // Atualizar disponibilidade do item do cardápio
        updateAvailabilityBasedOnStock(recipe.menuItem)
    }
    
    @Transactional
    fun updateAvailabilityBasedOnStock(menuItem: MenuItem): MenuItem {
        val recipes = recipeRepository.findByMenuItem(menuItem)
        
        // Se não há receitas, o item continua disponível
        if (recipes.isEmpty()) {
            return menuItem
        }
        
        // Verificar se todos os ingredientes estão disponíveis em quantidade suficiente
        val isAvailable = recipes.all { recipe ->
            recipe.stockItem.quantity >= recipe.quantity
        }
        
        // Atualizar a disponibilidade apenas se for diferente do estado atual
        if (menuItem.available != isAvailable) {
            menuItem.available = isAvailable
            menuItem.updatedAt = LocalDateTime.now()
            menuItemRepository.save(menuItem)
        }
        
        return menuItem
    }
    
    @Transactional
    fun updateAllAvailability() {
        val allMenuItems = menuItemRepository.findAll()
        allMenuItems.forEach { updateAvailabilityBasedOnStock(it) }
    }
    
    @Transactional
    fun delete(id: Long) {
        val menuItem = findById(id)
        
        // As receitas serão excluídas automaticamente devido ao CascadeType.ALL
        menuItemRepository.delete(menuItem)
    }
} 