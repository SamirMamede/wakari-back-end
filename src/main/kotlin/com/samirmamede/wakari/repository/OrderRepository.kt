package com.samirmamede.wakari.repository

import com.samirmamede.wakari.model.Order
import com.samirmamede.wakari.model.OrderStatus
import com.samirmamede.wakari.model.User
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
interface OrderRepository : JpaRepository<Order, Long> {
    fun findByUser(user: User, pageable: Pageable): Page<Order>
    fun findByStatus(status: OrderStatus, pageable: Pageable): Page<Order>
    fun findByCreatedAtBetween(startDate: LocalDateTime, endDate: LocalDateTime, pageable: Pageable): Page<Order>
} 