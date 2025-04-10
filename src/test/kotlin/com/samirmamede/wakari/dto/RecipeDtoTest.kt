package com.samirmamede.wakari.dto

import com.samirmamede.wakari.dto.RecipeRequest
import com.samirmamede.wakari.dto.RecipeResponse
import com.samirmamede.wakari.dto.RecipeUpdateRequest
import com.samirmamede.wakari.model.MenuItem
import com.samirmamede.wakari.model.Recipe
import com.samirmamede.wakari.model.StockItem
import jakarta.validation.Validation
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.time.LocalDateTime

class RecipeDtoTest {

    private val validator = Validation.buildDefaultValidatorFactory().validator

    private val testMenuItem = MenuItem(
        id = 1,
        name = "Test Item",
        description = "Test Description",
        price = BigDecimal("10.00"),
        category = "Test Category",
        available = true,
        imageUrl = "test.jpg",
        createdAt = LocalDateTime.now(),
        updatedAt = LocalDateTime.now()
    )
    
    private val testStockItem = StockItem(
        id = 1,
        name = "Test Stock",
        quantity = BigDecimal("100.00"),
        unit = "kg",
        minQuantity = BigDecimal("10.00"),
        createdAt = LocalDateTime.now(),
        updatedAt = LocalDateTime.now()
    )
    
    private val testQuantity = BigDecimal("2.5")
    private val testCost = BigDecimal("10.00")
    private val testCreatedAt = LocalDateTime.now()
    private val testUpdatedAt = LocalDateTime.now()

    @Test
    fun `RecipeRequest should validate required fields`() {
        val request = RecipeRequest(
            menuItemId = 1,
            stockItemId = 1,
            quantity = testQuantity,
            cost = testCost
        )

        val violations = validator.validate(request)
        assertTrue(violations.isEmpty())
    }

    @Test
    fun `RecipeRequest should accept valid values`() {
        val request = RecipeRequest(
            menuItemId = 1,
            stockItemId = 1,
            quantity = BigDecimal("1.5"),
            cost = BigDecimal("5.00")
        )

        val violations = validator.validate(request)
        assertTrue(violations.isEmpty())
        assertEquals(1L, request.menuItemId)
        assertEquals(1L, request.stockItemId)
        assertEquals(BigDecimal("1.5"), request.quantity)
        assertEquals(BigDecimal("5.00"), request.cost)
    }

    @Test
    fun `RecipeRequest should reject negative quantity`() {
        val request = RecipeRequest(
            menuItemId = 1,
            stockItemId = 1,
            quantity = BigDecimal("-1.0"),
            cost = BigDecimal("5.00")
        )

        val violations = validator.validate(request)
        assertFalse(violations.isEmpty())
        assertTrue(violations.any { it.propertyPath.toString() == "quantity" })
    }

    @Test
    fun `RecipeRequest should reject negative cost`() {
        val request = RecipeRequest(
            menuItemId = 1,
            stockItemId = 1,
            quantity = BigDecimal("1.0"),
            cost = BigDecimal("-5.00")
        )

        val violations = validator.validate(request)
        assertFalse(violations.isEmpty())
        assertTrue(violations.any { it.propertyPath.toString() == "cost" })
    }

    @Test
    fun `RecipeRequest should reject zero cost`() {
        val request = RecipeRequest(
            menuItemId = 1,
            stockItemId = 1,
            quantity = BigDecimal("1.0"),
            cost = BigDecimal.ZERO
        )

        val violations = validator.validate(request)
        assertFalse(violations.isEmpty())
        assertTrue(violations.any { it.propertyPath.toString() == "cost" })
    }

    @Test
    fun `RecipeUpdateRequest should validate required fields`() {
        val request = RecipeUpdateRequest(
            quantity = testQuantity,
            cost = testCost
        )

        val violations = validator.validate(request)
        assertTrue(violations.isEmpty())
    }

    @Test
    fun `RecipeUpdateRequest should accept valid values`() {
        val request = RecipeUpdateRequest(
            quantity = BigDecimal("1.5"),
            cost = BigDecimal("5.00")
        )

        val violations = validator.validate(request)
        assertTrue(violations.isEmpty())
        assertEquals(BigDecimal("1.5"), request.quantity)
        assertEquals(BigDecimal("5.00"), request.cost)
    }

    @Test
    fun `RecipeUpdateRequest should reject negative quantity`() {
        val request = RecipeUpdateRequest(
            quantity = BigDecimal("-1.0"),
            cost = BigDecimal("5.00")
        )

        val violations = validator.validate(request)
        assertFalse(violations.isEmpty())
        assertTrue(violations.any { it.propertyPath.toString() == "quantity" })
    }

    @Test
    fun `RecipeUpdateRequest should reject negative cost`() {
        val request = RecipeUpdateRequest(
            quantity = BigDecimal("1.0"),
            cost = BigDecimal("-5.00")
        )

        val violations = validator.validate(request)
        assertFalse(violations.isEmpty())
        assertTrue(violations.any { it.propertyPath.toString() == "cost" })
    }

    @Test
    fun `RecipeUpdateRequest should reject zero cost`() {
        val request = RecipeUpdateRequest(
            quantity = BigDecimal("1.0"),
            cost = BigDecimal.ZERO
        )

        val violations = validator.validate(request)
        assertFalse(violations.isEmpty())
        assertTrue(violations.any { it.propertyPath.toString() == "cost" })
    }

    @Test
    fun `RecipeResponse should map from entity correctly`() {
        val recipe = Recipe(
            id = 1,
            menuItem = testMenuItem,
            stockItem = testStockItem,
            quantity = testQuantity,
            cost = testCost,
            createdAt = testCreatedAt,
            updatedAt = testUpdatedAt
        )

        val response = RecipeResponse(
            id = recipe.id,
            menuItemId = recipe.menuItem.id,
            stockItemId = recipe.stockItem.id,
            quantity = recipe.quantity,
            cost = recipe.cost,
            createdAt = recipe.createdAt,
            updatedAt = recipe.updatedAt
        )

        assertEquals(recipe.id, response.id)
        assertEquals(recipe.menuItem.id, response.menuItemId)
        assertEquals(recipe.stockItem.id, response.stockItemId)
        assertEquals(recipe.quantity, response.quantity)
        assertEquals(recipe.cost, response.cost)
        assertEquals(recipe.createdAt, response.createdAt)
        assertEquals(recipe.updatedAt, response.updatedAt)
    }

    @Test
    fun `RecipeResponse should handle large cost values correctly`() {
        val largeCost = BigDecimal("999999.99")
        val recipe = Recipe(
            id = 1,
            menuItem = testMenuItem,
            stockItem = testStockItem,
            quantity = testQuantity,
            cost = largeCost,
            createdAt = testCreatedAt,
            updatedAt = testUpdatedAt
        )

        val response = RecipeResponse.fromEntity(recipe)
        assertEquals(largeCost, response.cost)
    }
}