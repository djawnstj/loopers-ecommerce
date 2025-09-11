package com.loopers.infrastructure.order

import com.loopers.domain.order.Order
import com.loopers.domain.order.OrderRepository
import org.springframework.stereotype.Component

@Component
class OrderRepositoryImpl(
    private val jpaOrderRepository: JpaOrderRepository,
) : OrderRepository {
    override fun findByIdWithItems(orderId: Long): Order? {
        return jpaOrderRepository.findByIdWithItems(orderId)
    }
}