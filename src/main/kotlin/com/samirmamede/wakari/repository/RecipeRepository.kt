package com.samirmamede.wakari.repository

import com.samirmamede.wakari.model.MenuItem
import com.samirmamede.wakari.model.Recipe
import com.samirmamede.wakari.model.StockItem
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface RecipeRepository : JpaRepository<Recipe, Long> {
    fun findByMenuItem(menuItem: MenuItem): List<Recipe>
    fun findByStockItem(stockItem: StockItem): List<Recipe>
    fun findByMenuItemIdAndStockItemId(menuItemId: Long, stockItemId: Long): Recipe?
    fun findByMenuItemId(menuItemId: Long): List<Recipe>
    fun findByStockItemId(stockItemId: Long): List<Recipe>
} 