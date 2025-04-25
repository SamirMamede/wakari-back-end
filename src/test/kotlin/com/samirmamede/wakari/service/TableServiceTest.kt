package com.samirmamede.wakari.service

import com.samirmamede.wakari.exception.ResourceAlreadyExistsException
import com.samirmamede.wakari.exception.ResourceNotFoundException
import com.samirmamede.wakari.model.RestaurantTable
import com.samirmamede.wakari.model.TableStatus
import com.samirmamede.wakari.repository.TableRepository
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import java.util.*

@ExtendWith(MockitoExtension::class)
class TableServiceTest {

    @Mock
    private lateinit var tableRepository: TableRepository

    @InjectMocks
    private lateinit var tableService: TableService

    private lateinit var table1: RestaurantTable
    private lateinit var table2: RestaurantTable
    private lateinit var pageable: PageRequest

    @BeforeEach
    fun setup() {
        table1 = RestaurantTable(
            id = 1L,
            number = 1,
            capacity = 4,
            status = TableStatus.AVAILABLE
        )
        
        table2 = RestaurantTable(
            id = 2L,
            number = 2,
            capacity = 6,
            status = TableStatus.OCCUPIED
        )
        
        pageable = PageRequest.of(0, 10, Sort.by("number"))
    }

    @Test
    fun `deve retornar todas as mesas paginadas`() {
        // Arrange
        val tables = listOf(table1, table2)
        val page = PageImpl(tables, pageable, tables.size.toLong())
        `when`(tableRepository.findAll(pageable)).thenReturn(page)

        // Act
        val result = tableService.findAll(pageable)

        // Assert
        assertEquals(2, result.content.size)
        assertEquals(1, result.content[0].number)
        assertEquals(2, result.content[1].number)
        verify(tableRepository, times(1)).findAll(pageable)
    }

    @Test
    fun `deve retornar mesa por ID`() {
        // Arrange
        `when`(tableRepository.findById(1L)).thenReturn(Optional.of(table1))

        // Act
        val result = tableService.findById(1L)

        // Assert
        assertEquals(1L, result.id)
        assertEquals(1, result.number)
        verify(tableRepository, times(1)).findById(1L)
    }

    @Test
    fun `deve lançar exceção quando mesa não for encontrada pelo ID`() {
        // Arrange
        `when`(tableRepository.findById(99L)).thenReturn(Optional.empty())

        // Act & Assert
        val exception = assertThrows(ResourceNotFoundException::class.java) {
            tableService.findById(99L)
        }
        assertEquals("Mesa não encontrada com ID: 99", exception.message)
        verify(tableRepository, times(1)).findById(99L)
    }

    @Test
    fun `deve retornar mesa por número`() {
        // Arrange
        `when`(tableRepository.findByNumber(1)).thenReturn(Optional.of(table1))

        // Act
        val result = tableService.findByNumber(1)

        // Assert
        assertNotNull(result)
        assertEquals(1, result?.number)
        verify(tableRepository, times(1)).findByNumber(1)
    }

    @Test
    fun `deve retornar nulo quando mesa não for encontrada pelo número`() {
        // Arrange
        `when`(tableRepository.findByNumber(99)).thenReturn(Optional.empty())

        // Act
        val result = tableService.findByNumber(99)

        // Assert
        assertNull(result)
        verify(tableRepository, times(1)).findByNumber(99)
    }

    @Test
    fun `deve retornar mesas por status`() {
        // Arrange
        `when`(tableRepository.findByStatus(TableStatus.AVAILABLE)).thenReturn(listOf(table1))

        // Act
        val result = tableService.findByStatus(TableStatus.AVAILABLE)

        // Assert
        assertEquals(1, result.size)
        assertEquals(TableStatus.AVAILABLE, result[0].status)
        verify(tableRepository, times(1)).findByStatus(TableStatus.AVAILABLE)
    }

    @Test
    fun `deve criar nova mesa`() {
        // Arrange
        val newTable = RestaurantTable(
            number = 3,
            capacity = 2,
            status = TableStatus.AVAILABLE
        )
        `when`(tableRepository.findByNumber(3)).thenReturn(Optional.empty())
        `when`(tableRepository.save(any())).thenReturn(newTable.copy(id = 3L))

        // Act
        val result = tableService.create(newTable)

        // Assert
        assertEquals(3, result.number)
        assertEquals(2, result.capacity)
        assertEquals(TableStatus.AVAILABLE, result.status)
        verify(tableRepository, times(1)).findByNumber(3)
        verify(tableRepository, times(1)).save(any())
    }

    @Test
    fun `deve lançar exceção ao tentar criar mesa com número já existente`() {
        // Arrange
        val newTable = RestaurantTable(
            number = 1,
            capacity = 4,
            status = TableStatus.AVAILABLE
        )
        `when`(tableRepository.findByNumber(1)).thenReturn(Optional.of(table1))

        // Act & Assert
        val exception = assertThrows(ResourceAlreadyExistsException::class.java) {
            tableService.create(newTable)
        }
        assertEquals("Já existe uma mesa com o número 1", exception.message)
        verify(tableRepository, times(1)).findByNumber(1)
        verify(tableRepository, never()).save(any())
    }

    @Test
    fun `deve atualizar mesa existente`() {
        // Arrange
        val updatedTable = RestaurantTable(
            id = 1L,
            number = 1,
            capacity = 8,
            status = TableStatus.RESERVED
        )
        `when`(tableRepository.findById(1L)).thenReturn(Optional.of(table1))
        `when`(tableRepository.save(any())).thenReturn(updatedTable)

        // Act
        val result = tableService.update(1L, updatedTable)

        // Assert
        assertEquals(1L, result.id)
        assertEquals(1, result.number)
        assertEquals(8, result.capacity)
        assertEquals(TableStatus.RESERVED, result.status)
        verify(tableRepository, times(1)).findById(1L)
        verify(tableRepository, times(1)).save(any())
    }

    @Test
    fun `deve alterar status da mesa`() {
        // Arrange
        val updatedTable = table1.copy(status = TableStatus.OCCUPIED)
        `when`(tableRepository.findById(1L)).thenReturn(Optional.of(table1))
        `when`(tableRepository.save(any())).thenReturn(updatedTable)

        // Act
        val result = tableService.updateStatus(1L, TableStatus.OCCUPIED)

        // Assert
        assertEquals(1L, result.id)
        assertEquals(TableStatus.OCCUPIED, result.status)
        verify(tableRepository, times(1)).findById(1L)
        verify(tableRepository, times(1)).save(any())
    }

    @Test
    fun `deve limpar mesas em status de limpeza`() {
        // Arrange
        val cleaningTable1 = RestaurantTable(
            id = 3L,
            number = 3,
            capacity = 4,
            status = TableStatus.CLEANING
        )
        val cleaningTable2 = RestaurantTable(
            id = 4L,
            number = 4,
            capacity = 6,
            status = TableStatus.CLEANING
        )
        
        `when`(tableRepository.findByStatus(TableStatus.CLEANING)).thenReturn(listOf(cleaningTable1, cleaningTable2))
        `when`(tableRepository.save(any())).thenAnswer { invocation -> invocation.getArgument(0) }

        // Act
        tableService.cleanupTables()

        // Assert
        verify(tableRepository, times(1)).findByStatus(TableStatus.CLEANING)
        verify(tableRepository, times(2)).save(any())
    }
} 