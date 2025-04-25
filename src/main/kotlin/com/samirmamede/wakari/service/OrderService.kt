package com.samirmamede.wakari.service

import com.samirmamede.wakari.dto.OrderItemRequest
import com.samirmamede.wakari.exception.InvalidOperationException
import com.samirmamede.wakari.exception.ResourceNotFoundException
import com.samirmamede.wakari.model.*
import com.samirmamede.wakari.repository.MenuItemRepository
import com.samirmamede.wakari.repository.OrderItemRepository
import com.samirmamede.wakari.repository.OrderRepository
import com.samirmamede.wakari.repository.TableRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

@Service
class OrderService(
    private val orderRepository: OrderRepository,
    private val orderItemRepository: OrderItemRepository,
    private val tableRepository: TableRepository,
    private val menuItemRepository: MenuItemRepository,
    private val userService: UserService,
    private val tableService: TableService,
    private val stockItemService: StockItemService
) {
    /**
     * Busca todos os pedidos com paginação
     */
    fun findAll(pageable: Pageable): Page<Order> = 
        orderRepository.findAll(pageable)
    
    /**
     * Busca um pedido pelo ID
     */
    fun findById(id: Long): Order = 
        orderRepository.findById(id)
            .orElseThrow { ResourceNotFoundException("Pedido não encontrado com ID: $id") }
    
    /**
     * Busca pedidos por status com paginação
     */
    fun findByStatus(status: OrderStatus, pageable: Pageable): Page<Order> = 
        orderRepository.findByStatus(status, pageable)
    
    /**
     * Busca pedidos de um usuário específico com paginação
     */
    fun findByUser(userId: Long, pageable: Pageable): Page<Order> {
        val user = userService.findById(userId)
        return orderRepository.findByUser(user, pageable)
    }
    
    /**
     * Busca pedidos por período
     */
    fun findByDateRange(startDate: LocalDate, endDate: LocalDate, pageable: Pageable): Page<Order> {
        val startDateTime = startDate.atStartOfDay()
        val endDateTime = endDate.atTime(LocalTime.MAX)
        return orderRepository.findByCreatedAtBetween(startDateTime, endDateTime, pageable)
    }
    
    /**
     * Cria um novo pedido
     */
    @Transactional
    fun create(userId: Long, tableId: Long?, isDelivery: Boolean, items: List<OrderItemRequest>): Order {
        // Verificar se o usuário existe
        val user = userService.findById(userId)
        
        // Inicializar pedido
        val order = Order(
            user = user,
            table = tableId?.let { 
                // Se tem mesa, verifica se está disponível
                val table = tableService.findById(it)
                if (table.status != TableStatus.AVAILABLE && table.status != TableStatus.RESERVED) {
                    throw InvalidOperationException("Mesa ${table.number} não está disponível para novos pedidos")
                }
                
                // Atualiza o status da mesa para ocupada
                tableService.updateStatus(table.id, TableStatus.OCCUPIED)
                
                // Define o pedido atual da mesa
                table.currentOrder = order
                tableRepository.save(table)
                
                table
            },
            isDelivery = isDelivery,
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )
        
        // Salvar pedido
        val savedOrder = orderRepository.save(order)
        
        // Adicionar itens ao pedido
        addItemsToOrder(savedOrder, items)
        
        // Recalcular o total
        savedOrder.calculateTotal()
        
        return orderRepository.save(savedOrder)
    }
    
    /**
     * Adiciona itens a um pedido existente
     */
    @Transactional
    fun addItemsToOrder(orderId: Long, items: List<OrderItemRequest>): Order {
        val order = findById(orderId)
        
        if (order.status != OrderStatus.PENDING) {
            throw InvalidOperationException("Não é possível adicionar itens a um pedido que não está em estado pendente")
        }
        
        addItemsToOrder(order, items)
        
        // Recalcular o total
        order.calculateTotal()
        
        return orderRepository.save(order)
    }
    
    /**
     * Função auxiliar para adicionar itens ao pedido
     */
    private fun addItemsToOrder(order: Order, itemRequests: List<OrderItemRequest>) {
        for (itemRequest in itemRequests) {
            val menuItem = menuItemRepository.findById(itemRequest.menuItemId)
                .orElseThrow { ResourceNotFoundException("Item do cardápio não encontrado com ID: ${itemRequest.menuItemId}") }
            
            if (!menuItem.available) {
                throw InvalidOperationException("Item do cardápio '${menuItem.name}' não está disponível")
            }
            
            val orderItem = OrderItem(
                order = order,
                menuItem = menuItem,
                quantity = itemRequest.quantity,
                priceAtTime = menuItem.price,
                notes = itemRequest.notes,
                createdAt = LocalDateTime.now(),
                updatedAt = LocalDateTime.now()
            )
            
            order.items.add(orderItem)
        }
    }
    
    /**
     * Atualiza o status de um pedido
     */
    @Transactional
    fun updateStatus(id: Long, newStatus: OrderStatus): Order {
        val order = findById(id)
        
        // Verificar transições de estado válidas
        validateStatusTransition(order.status, newStatus)
        
        val previousStatus = order.status
        order.status = newStatus
        order.updatedAt = LocalDateTime.now()
        
        // Se o pedido for finalizado (DELIVERED), atualizar estoque
        if (newStatus == OrderStatus.DELIVERED && previousStatus != OrderStatus.DELIVERED) {
            updateStockAfterDelivery(order)
        }
        
        // Se o pedido for cancelado, liberar mesa se associada
        if (newStatus == OrderStatus.CANCELED && order.table != null) {
            val table = order.table
            table.currentOrder = null
            table.status = TableStatus.CLEANING
            tableRepository.save(table)
        }
        
        // Se o pedido for entregue, liberar mesa para limpeza
        if (newStatus == OrderStatus.DELIVERED && order.table != null) {
            val table = order.table
            table.currentOrder = null
            table.status = TableStatus.CLEANING
            tableRepository.save(table)
        }
        
        return orderRepository.save(order)
    }
    
    /**
     * Validação de transições de estado
     */
    private fun validateStatusTransition(currentStatus: OrderStatus, newStatus: OrderStatus) {
        when (currentStatus) {
            OrderStatus.PENDING -> {
                if (newStatus != OrderStatus.PREPARING && newStatus != OrderStatus.CANCELED) {
                    throw InvalidOperationException("Um pedido pendente só pode ser alterado para 'em preparo' ou 'cancelado'")
                }
            }
            OrderStatus.PREPARING -> {
                if (newStatus != OrderStatus.READY && newStatus != OrderStatus.CANCELED) {
                    throw InvalidOperationException("Um pedido em preparo só pode ser alterado para 'pronto' ou 'cancelado'")
                }
            }
            OrderStatus.READY -> {
                if (newStatus != OrderStatus.DELIVERED && newStatus != OrderStatus.CANCELED) {
                    throw InvalidOperationException("Um pedido pronto só pode ser alterado para 'entregue' ou 'cancelado'")
                }
            }
            OrderStatus.DELIVERED, OrderStatus.CANCELED -> {
                throw InvalidOperationException("Não é possível alterar o status de um pedido já finalizado ou cancelado")
            }
        }
    }
    
    /**
     * Atualiza o estoque após a entrega de um pedido
     */
    private fun updateStockAfterDelivery(order: Order) {
        // Para cada item do pedido
        for (orderItem in order.items) {
            val menuItem = orderItem.menuItem
            
            // Para cada ingrediente (receita) do item do cardápio
            for (recipe in menuItem.recipes) {
                val stockItem = recipe.stockItem
                val quantityToRemove = recipe.quantity.multiply(orderItem.quantity.toBigDecimal())
                
                // Remover quantidade do estoque
                val newQuantity = stockItem.quantity.subtract(quantityToRemove)
                
                // Atualizar estoque
                stockItemService.updateQuantity(stockItem.id, newQuantity)
            }
        }
    }
    
    /**
     * Cancela um pedido
     */
    @Transactional
    fun cancelOrder(id: Long): Order {
        return updateStatus(id, OrderStatus.CANCELED)
    }
} 