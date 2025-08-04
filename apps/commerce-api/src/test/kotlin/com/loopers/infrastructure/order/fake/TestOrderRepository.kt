package com.loopers.infrastructure.order.fake

import com.loopers.domain.order.Order
import com.loopers.domain.order.OrderRepository

class TestOrderRepository : OrderRepository {
    private val orders = mutableListOf<Order>()
    private var nextId = 1L

    override fun save(order: Order): Order {
        val idField = order.javaClass.superclass.getDeclaredField("id")
        idField.isAccessible = true
        idField.set(order, nextId++)

        orders.add(order)
        return order
    }

    fun findAll(): List<Order> = orders.toList()

    fun clear() {
        orders.clear()
        nextId = 1L
    }
}
