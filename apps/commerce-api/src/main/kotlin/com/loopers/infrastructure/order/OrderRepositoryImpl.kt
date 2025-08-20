package com.loopers.infrastructure.order

import com.loopers.domain.order.Order
import com.loopers.domain.order.OrderRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Component

@Component
class OrderRepositoryImpl(
    private val jpaOrderRepository: JpaOrderRepository,
) : OrderRepository {
    override fun save(order: Order): Order = jpaOrderRepository.save(order)

    override fun findById(id: Long): Order? = jpaOrderRepository.findByIdOrNull(id)
}
