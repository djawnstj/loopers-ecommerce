package com.loopers.infrastructure.order

import com.loopers.domain.order.Order
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface JpaOrderRepository : JpaRepository<Order, Long> {
    @Query("SELECT o FROM Order o JOIN FETCH o.orderItems WHERE o.id = :orderId")
    fun findByIdWithItems(orderId: Long): Order?
}
