package com.loopers.fixture.order

import com.loopers.domain.order.Order
import java.math.BigDecimal

sealed class OrderFixture(
    val userId: Long = 1L,
    val totalAmount: BigDecimal = BigDecimal("10000"),
    val payPrice: BigDecimal = BigDecimal("10000"),
) {
    data object 기본 : OrderFixture()
    data object `1만원 주문` : OrderFixture(totalAmount = BigDecimal("10000"), payPrice = BigDecimal("10000"))
    data object `3만원 주문` : OrderFixture(totalAmount = BigDecimal("30000"), payPrice = BigDecimal("30000"))

    fun toEntity(): Order = Order(userId, totalAmount, payPrice)
}
