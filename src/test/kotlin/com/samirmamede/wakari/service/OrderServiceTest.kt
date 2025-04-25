package com.samirmamede.wakari.service

import com.samirmamede.wakari.dto.OrderItemRequest
import com.samirmamede.wakari.exception.InvalidOperationException
import com.samirmamede.wakari.exception.ResourceNotFoundException
import com.samirmamede.wakari.model.*
import com.samirmamede.wakari.repository.MenuItemRepository
import com.samirmamede.wakari.repository.OrderItemRepository
import com.samirmamede.wakari.repository.OrderRepository
import com.samirmamede.wakari.repository.TableRepository
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentCaptor
import org.mockito.Captor
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.*

@ExtendWith(MockitoExtension::class)
class OrderServiceTest {

    @Mock
    private lateinit var orderRepository: OrderRepository

    @Mock
    private lateinit var orderItemRepository: OrderItemRepository

    @Mock
    private lateinit var tableRepository: TableRepository

    @Mock
    private lateinit var menuItemRepository: MenuItemRepository

    @Mock
    private lateinit var userService: UserService

    @Mock
    private lateinit var tableService: TableService

    @Mock
    private lateinit var stockItemService: StockItemService

    @InjectMocks
    private lateinit var orderService: OrderService

    @Captor
    private lateinit var orderArgumentCaptor: ArgumentCaptor<Order>

    private lateinit var testUser: User
    private lateinit var testTable: RestaurantTable
    private lateinit var testMenuItem: MenuItem
    private lateinit var testOrder: Order

    @BeforeEach
    fun setup() {
        // Configuração do usuário de teste
        testUser = User(
            id = 1L,
            name = "Usuário Teste",
            email = "usuario@teste.com",
            password = "senha123",
            role = UserRole.CUSTOMER
        )

        // Configuração da mesa de teste
        testTable = RestaurantTable(
            id = 1L,
            number = 10,
            capacity = 4,
            status = TableStatus.AVAILABLE
        )

        // Configuração do item de menu de teste
        testMenuItem = MenuItem(
            id = 1L,
            name = "Produto Teste",
            description = "Descrição do produto",
            price = BigDecimal("15.90"),
            available = true,
            category = "Categoria Teste"
        )

        // Configuração do pedido de teste
        testOrder = Order(
            id = 1L,
            user = testUser,
            table = testTable,
            total = BigDecimal.ZERO,
            status = OrderStatus.PENDING,
            isDelivery = false
        )

        // Configurar o comportamento padrão dos mocks
        `when`(userService.findById(anyLong())).thenReturn(testUser)
        `when`(tableService.findById(anyLong())).thenReturn(testTable)
        `when`(menuItemRepository.findById(anyLong())).thenReturn(Optional.of(testMenuItem))
        `when`(orderRepository.save(any(Order::class.java))).thenAnswer { it.arguments[0] as Order }
        `when`(orderRepository.findById(anyLong())).thenReturn(Optional.of(testOrder))
    }

    @Test
    fun `findById should return order when it exists`() {
        // Arrange
        val orderId = 1L
        
        // Act
        val result = orderService.findById(orderId)
        
        // Assert
        assertEquals(testOrder, result)
        verify(orderRepository).findById(orderId)
    }

    @Test
    fun `findById should throw ResourceNotFoundException when order does not exist`() {
        // Arrange
        val orderId = 999L
        `when`(orderRepository.findById(orderId)).thenReturn(Optional.empty())
        
        // Act & Assert
        val exception = assertThrows(ResourceNotFoundException::class.java) {
            orderService.findById(orderId)
        }
        
        assertTrue(exception.message!!.contains("Pedido não encontrado"))
    }

    @Test
    fun `create should create a new order with delivery option`() {
        // Arrange
        val userId = 1L
        val tableId = null
        val isDelivery = true
        val items = listOf(
            OrderItemRequest(
                menuItemId = 1L,
                quantity = 2
            )
        )
        
        // Act
        val result = orderService.create(userId, tableId, isDelivery, items)
        
        // Assert
        verify(orderRepository, times(2)).save(any(Order::class.java))
        assertEquals(userId, result.user.id)
        assertEquals(isDelivery, result.isDelivery)
        assertNull(result.table)
        assertEquals(OrderStatus.PENDING, result.status)
        assertEquals(1, result.items.size)
    }

    @Test
    fun `create should create a new order with table association`() {
        // Arrange
        val userId = 1L
        val tableId = 1L
        val isDelivery = false
        val items = listOf(
            OrderItemRequest(
                menuItemId = 1L,
                quantity = 2
            )
        )
        
        // Act
        val result = orderService.create(userId, tableId, isDelivery, items)
        
        // Assert
        verify(orderRepository, times(2)).save(any(Order::class.java))
        verify(tableService).updateStatus(tableId, TableStatus.OCCUPIED)
        assertEquals(userId, result.user.id)
        assertEquals(isDelivery, result.isDelivery)
        assertNotNull(result.table)
        assertEquals(tableId, result.table?.id)
        assertEquals(OrderStatus.PENDING, result.status)
        assertEquals(1, result.items.size)
    }

    @Test
    fun `create should throw InvalidOperationException when table is not available`() {
        // Arrange
        val userId = 1L
        val tableId = 1L
        val isDelivery = false
        val items = listOf(
            OrderItemRequest(
                menuItemId = 1L,
                quantity = 2
            )
        )
        
        // Configurar mesa como ocupada
        val occupiedTable = testTable.copy(status = TableStatus.OCCUPIED)
        `when`(tableService.findById(tableId)).thenReturn(occupiedTable)
        
        // Act & Assert
        val exception = assertThrows(InvalidOperationException::class.java) {
            orderService.create(userId, tableId, isDelivery, items)
        }
        
        assertTrue(exception.message!!.contains("não está disponível"))
    }

    @Test
    fun `addItemsToOrder should add items to existing order`() {
        // Arrange
        val orderId = 1L
        val items = listOf(
            OrderItemRequest(
                menuItemId = 1L,
                quantity = 3
            )
        )
        
        // Act
        val result = orderService.addItemsToOrder(orderId, items)
        
        // Assert
        verify(orderRepository).save(any(Order::class.java))
        assertEquals(1, result.items.size)
        assertEquals(3, result.items[0].quantity)
    }

    @Test
    fun `addItemsToOrder should throw InvalidOperationException when order is not pending`() {
        // Arrange
        val orderId = 1L
        val items = listOf(
            OrderItemRequest(
                menuItemId = 1L,
                quantity = 3
            )
        )
        
        // Configurar pedido como já em preparo
        val preparingOrder = testOrder.copy(status = OrderStatus.PREPARING)
        `when`(orderRepository.findById(orderId)).thenReturn(Optional.of(preparingOrder))
        
        // Act & Assert
        val exception = assertThrows(InvalidOperationException::class.java) {
            orderService.addItemsToOrder(orderId, items)
        }
        
        assertTrue(exception.message!!.contains("não está em estado pendente"))
    }

    @Test
    fun `addItemsToOrder should throw InvalidOperationException when menu item is not available`() {
        // Arrange
        val orderId = 1L
        val items = listOf(
            OrderItemRequest(
                menuItemId = 1L,
                quantity = 3
            )
        )
        
        // Configurar item do menu como não disponível
        val unavailableMenuItem = testMenuItem.copy(available = false)
        `when`(menuItemRepository.findById(1L)).thenReturn(Optional.of(unavailableMenuItem))
        
        // Act & Assert
        val exception = assertThrows(InvalidOperationException::class.java) {
            orderService.addItemsToOrder(orderId, items)
        }
        
        assertTrue(exception.message!!.contains("não está disponível"))
    }

    @Test
    fun `updateStatus should update order status from PENDING to PREPARING`() {
        // Arrange
        val orderId = 1L
        val newStatus = OrderStatus.PREPARING
        
        // Act
        val result = orderService.updateStatus(orderId, newStatus)
        
        // Assert
        verify(orderRepository).save(any(Order::class.java))
        assertEquals(newStatus, result.status)
    }

    @Test
    fun `updateStatus should update order status from PREPARING to READY`() {
        // Arrange
        val orderId = 1L
        val newStatus = OrderStatus.READY
        val preparingOrder = testOrder.copy(status = OrderStatus.PREPARING)
        `when`(orderRepository.findById(orderId)).thenReturn(Optional.of(preparingOrder))
        
        // Act
        val result = orderService.updateStatus(orderId, newStatus)
        
        // Assert
        verify(orderRepository).save(any(Order::class.java))
        assertEquals(newStatus, result.status)
    }

    @Test
    fun `updateStatus should throw InvalidOperationException for invalid transition`() {
        // Arrange
        val orderId = 1L
        val newStatus = OrderStatus.DELIVERED
        
        // Act & Assert
        val exception = assertThrows(InvalidOperationException::class.java) {
            orderService.updateStatus(orderId, newStatus)
        }
        
        assertTrue(exception.message!!.contains("só pode ser alterado para"))
    }

    @Test
    fun `updateStatus should update table status when order is delivered`() {
        // Arrange
        val orderId = 1L
        val newStatus = OrderStatus.DELIVERED
        val readyOrder = testOrder.copy(status = OrderStatus.READY)
        `when`(orderRepository.findById(orderId)).thenReturn(Optional.of(readyOrder))
        
        // Act
        val result = orderService.updateStatus(orderId, newStatus)
        
        // Assert
        verify(orderRepository).save(any(Order::class.java))
        verify(tableRepository).save(any(RestaurantTable::class.java))
        assertEquals(newStatus, result.status)
    }

    @Test
    fun `cancelOrder should cancel an order`() {
        // Arrange
        val orderId = 1L
        
        // Act
        val result = orderService.cancelOrder(orderId)
        
        // Assert
        verify(orderRepository).save(any(Order::class.java))
        assertEquals(OrderStatus.CANCELED, result.status)
    }

    @Test
    fun `findByDateRange should return orders in date range`() {
        // Arrange
        val startDate = LocalDate.now().minusDays(7)
        val endDate = LocalDate.now()
        val pageable = mock(Pageable::class.java)
        val orderPage = PageImpl(listOf(testOrder))
        
        `when`(orderRepository.findByCreatedAtBetween(
            any(LocalDateTime::class.java),
            any(LocalDateTime::class.java),
            eq(pageable)
        )).thenReturn(orderPage)
        
        // Act
        val result = orderService.findByDateRange(startDate, endDate, pageable)
        
        // Assert
        verify(orderRepository).findByCreatedAtBetween(
            eq(startDate.atStartOfDay()),
            eq(endDate.atTime(LocalTime.MAX)),
            eq(pageable)
        )
        assertEquals(1, result.totalElements)
        assertEquals(testOrder, result.content[0])
    }
} 