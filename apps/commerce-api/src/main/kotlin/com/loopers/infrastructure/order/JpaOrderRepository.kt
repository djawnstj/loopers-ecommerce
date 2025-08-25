package com.loopers.infrastructure.order

import com.loopers.domain.order.Order
import com.loopers.domain.order.vo.OrderStatusType
import org.springframework.data.jpa.repository.JpaRepository

interface JpaOrderRepository : JpaRepository<Order, Long> {
    fun findByOrderNumber(orderNumber: String): Order?
    fun findAllByStatus(status: OrderStatusType): List<Order>
}
