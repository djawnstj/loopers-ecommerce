package com.loopers.domain.order

import org.springframework.stereotype.Service

@Service
class OrderService(
    private val orderRepository: OrderRepository,
) {
    fun findByIdWithItems(orderId: Long): Order? {
        return orderRepository.findByIdWithItems(orderId)
    }
}