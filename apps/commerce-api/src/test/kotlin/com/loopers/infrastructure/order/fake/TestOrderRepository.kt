package com.loopers.infrastructure.order.fake

import com.loopers.domain.order.Order
import com.loopers.domain.order.OrderRepository
import com.loopers.domain.order.vo.OrderStatusType

class TestOrderRepository : OrderRepository {
    private val orders = mutableListOf<Order>()
    private var nextId = 1L

    override fun save(order: Order): Order {
        val existingOrder = orders.find { it.id == order.id }
        if (existingOrder != null) {
            // 기존 주문 업데이트
            orders.remove(existingOrder)
            orders.add(order)
            return order
        }

        // 새 주문 저장
        if (order.id == 0L) {
            val idField = order.javaClass.superclass.getDeclaredField("id")
            idField.isAccessible = true
            idField.set(order, nextId++)
        }

        orders.add(order)
        return order
    }

    override fun findById(id: Long): Order? {
        return orders.find { it.id == id }
    }

    override fun findByOrderNumber(orderNumber: String): Order? =
        orders.find { it.orderNumber == orderNumber }

    override fun findAllByStatus(status: OrderStatusType): List<Order> =
        this.orders.filter { it.status == status }

    fun findAll(): List<Order> = orders.toList()

    fun clear() {
        orders.clear()
        nextId = 1L
    }
}
