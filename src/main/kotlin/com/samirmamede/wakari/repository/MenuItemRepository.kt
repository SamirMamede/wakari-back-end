package com.samirmamede.wakari.repository

import com.samirmamede.wakari.model.MenuItem
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface MenuItemRepository : JpaRepository<MenuItem, Long> {
    fun findByAvailable(available: Boolean, pageable: Pageable): Page<MenuItem>
    fun findByCategory(category: String, pageable: Pageable): Page<MenuItem>
    
    @Query("SELECT DISTINCT m.category FROM MenuItem m ORDER BY m.category")
    fun findAllCategories(): List<String>
} 