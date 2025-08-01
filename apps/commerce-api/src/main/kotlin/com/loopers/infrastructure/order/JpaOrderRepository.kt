package com.loopers.infrastructure.order

import com.loopers.domain.order.Order
import org.springframework.data.jpa.repository.JpaRepository

interface JpaOrderRepository : JpaRepository<Order, Long>
