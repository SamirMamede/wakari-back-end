package com.samirmamede.wakari.service

import com.samirmamede.wakari.dto.RecipeRequest
import com.samirmamede.wakari.dto.RecipeUpdateRequest
import com.samirmamede.wakari.exception.ResourceNotFoundException
import com.samirmamede.wakari.model.MenuItem
import com.samirmamede.wakari.model.Recipe
import com.samirmamede.wakari.model.StockItem
import com.samirmamede.wakari.repository.MenuItemRepository
import com.samirmamede.wakari.repository.RecipeRepository
import com.samirmamede.wakari.repository.StockItemRepository
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.jupiter.MockitoExtension
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.*

@ExtendWith(MockitoExtension::class)
class RecipeServiceTest {

    @Mock
    private lateinit var recipeRepository: RecipeRepository

    @Mock
    private lateinit var menuItemRepository: MenuItemRepository

    @Mock
    private lateinit var stockItemRepository: StockItemRepository

    @InjectMocks
    private lateinit var recipeService: RecipeService

    private lateinit var testMenuItem: MenuItem
    private lateinit var testStockItem: StockItem
    private lateinit var testRecipe: Recipe
    private lateinit var testRecipeRequest: RecipeRequest

    @BeforeEach
    fun setup() {
        testMenuItem = MenuItem(
            id = 1L,
            name = "Test Item",
            description = "Test Description",
            price = BigDecimal("10.00"),
            category = "Test Category",
            available = true,
            imageUrl = "test.jpg",
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )

        testStockItem = StockItem(
            id = 1L,
            name = "Test Stock",
            quantity = BigDecimal("100.00"),
            unit = "kg",
            minQuantity = BigDecimal("10.00"),
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )

        testRecipe = Recipe(
            id = 1L,
            menuItem = testMenuItem,
            stockItem = testStockItem,
            quantity = BigDecimal("2.5"),
            cost = BigDecimal("10.00"),
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )

        testRecipeRequest = RecipeRequest(
            menuItemId = 1L,
            stockItemId = 1L,
            quantity = BigDecimal("2.5"),
            cost = BigDecimal("10.00")
        )
    }

    @Test
    fun `should find all recipes`() {
        `when`(recipeRepository.findAll()).thenReturn(listOf(testRecipe))
        
        val result = recipeService.findAll()
        
        assertEquals(1, result.size)
        assertEquals(testRecipe.id, result[0].id)
        verify(recipeRepository).findAll()
    }

    @Test
    fun `should find recipe by id`() {
        `when`(recipeRepository.findById(1L)).thenReturn(Optional.of(testRecipe))
        
        val result = recipeService.findById(1L)
        
        assertEquals(testRecipe.id, result.id)
        verify(recipeRepository).findById(1L)
    }

    @Test
    fun `should throw ResourceNotFoundException when recipe not found`() {
        `when`(recipeRepository.findById(1L)).thenReturn(Optional.empty())
        
        assertThrows(ResourceNotFoundException::class.java) {
            recipeService.findById(1L)
        }
        
        verify(recipeRepository).findById(1L)
    }

    @Test
    fun `should create recipe`() {
        `when`(menuItemRepository.findById(1L)).thenReturn(Optional.of(testMenuItem))
        `when`(stockItemRepository.findById(1L)).thenReturn(Optional.of(testStockItem))
        `when`(recipeRepository.save(any())).thenReturn(testRecipe)
        
        val result = recipeService.create(testRecipeRequest)
        
        assertEquals(testRecipe.id, result.id)
        verify(menuItemRepository).findById(1L)
        verify(stockItemRepository).findById(1L)
        verify(recipeRepository).save(any())
    }

    @Test
    fun `should throw ResourceNotFoundException when menu item not found during creation`() {
        `when`(menuItemRepository.findById(1L)).thenReturn(Optional.empty())
        
        assertThrows(ResourceNotFoundException::class.java) {
            recipeService.create(testRecipeRequest)
        }
        
        verify(menuItemRepository).findById(1L)
        verify(stockItemRepository, never()).findById(any())
        verify(recipeRepository, never()).save(any())
    }

    @Test
    fun `should throw ResourceNotFoundException when stock item not found during creation`() {
        `when`(menuItemRepository.findById(1L)).thenReturn(Optional.of(testMenuItem))
        `when`(stockItemRepository.findById(1L)).thenReturn(Optional.empty())
        
        assertThrows(ResourceNotFoundException::class.java) {
            recipeService.create(testRecipeRequest)
        }
        
        verify(menuItemRepository).findById(1L)
        verify(stockItemRepository).findById(1L)
        verify(recipeRepository, never()).save(any())
    }

    @Test
    fun `should update recipe`() {
        `when`(recipeRepository.findById(1L)).thenReturn(Optional.of(testRecipe))
        `when`(recipeRepository.save(any())).thenReturn(testRecipe)
        
        val updateRequest = RecipeUpdateRequest(
            quantity = BigDecimal("2.5"),
            cost = BigDecimal("10.00")
        )
        
        val result = recipeService.update(1L, updateRequest)
        
        assertEquals(testRecipe.id, result.id)
        verify(recipeRepository).findById(1L)
        verify(recipeRepository).save(any())
    }

    @Test
    fun `should throw ResourceNotFoundException when updating non-existent recipe`() {
        `when`(recipeRepository.findById(1L)).thenReturn(Optional.empty())
        
        val updateRequest = RecipeUpdateRequest(
            quantity = BigDecimal("2.5"),
            cost = BigDecimal("10.00")
        )
        
        assertThrows(ResourceNotFoundException::class.java) {
            recipeService.update(1L, updateRequest)
        }
        
        verify(recipeRepository).findById(1L)
        verify(recipeRepository, never()).save(any())
    }

    @Test
    fun `should delete recipe`() {
        // Arrange
        `when`(recipeRepository.findById(1L)).thenReturn(Optional.of(testRecipe))
        
        // Act
        recipeService.delete(1L)
        
        // Assert
        verify(recipeRepository).findById(1L)
        verify(recipeRepository).deleteById(1L)
    }

    @Test
    fun `should throw ResourceNotFoundException when deleting non-existent recipe`() {
        `when`(recipeRepository.findById(1L)).thenReturn(Optional.empty())
        
        assertThrows(ResourceNotFoundException::class.java) {
            recipeService.delete(1L)
        }
        
        verify(recipeRepository).findById(1L)
        verify(recipeRepository, never()).deleteById(any())
    }

    @Test
    fun `should update recipe cost only`() {
        val newCost = BigDecimal("15.00")
        val updatedRecipe = testRecipe.copy(cost = newCost)
        
        `when`(recipeRepository.findById(1L)).thenReturn(Optional.of(testRecipe))
        `when`(recipeRepository.save(any())).thenReturn(updatedRecipe)
        
        val request = RecipeUpdateRequest(
            quantity = testRecipe.quantity,
            cost = newCost
        )
        
        val result = recipeService.update(1L, request)
        
        assertEquals(newCost, result.cost)
        assertEquals(testRecipe.quantity, result.quantity)
        verify(recipeRepository).findById(1L)
        verify(recipeRepository).save(any())
    }

    @Test
    fun `should calculate total cost correctly`() {
        val quantity = BigDecimal("2.5")
        val unitCost = BigDecimal("10.00")
        val expectedTotalCost = BigDecimal("25.00")
        
        val recipe = Recipe(
            id = 1L,
            menuItem = testMenuItem,
            stockItem = testStockItem,
            quantity = quantity,
            cost = unitCost,
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )
        
        val totalCost = recipe.quantity.multiply(recipe.cost).setScale(2, java.math.RoundingMode.HALF_EVEN)
        
        assertEquals(expectedTotalCost, totalCost)
    }
}