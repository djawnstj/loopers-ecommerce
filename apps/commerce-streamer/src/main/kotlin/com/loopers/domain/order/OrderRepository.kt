package com.loopers.domain.order

interface OrderRepository {
    fun findByIdWithItems(orderId: Long): Order?
}