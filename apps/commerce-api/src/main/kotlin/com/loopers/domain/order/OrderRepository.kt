package com.loopers.domain.order

import com.loopers.domain.order.vo.OrderStatusType

interface OrderRepository {
    fun save(order: Order): Order
    fun findById(id: Long): Order?
    fun findByOrderNumber(orderNumber: String): Order?
    fun findAllByStatus(status: OrderStatusType): List<Order>
}
