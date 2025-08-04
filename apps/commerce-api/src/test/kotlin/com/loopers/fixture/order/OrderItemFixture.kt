package com.loopers.fixture.order

import com.loopers.domain.order.Order
import com.loopers.domain.order.OrderItem
import java.math.BigDecimal

sealed class OrderItemFixture(
    val productItemId: Long = 1L,
    val productName: String = "상품명",
    val productPrice: BigDecimal = BigDecimal("10000"),
    val quantity: Int = 1,
) {
    data object 기본 : OrderItemFixture()
    data object `상품1` : OrderItemFixture(productItemId = 1L, productName = "상품1", productPrice = BigDecimal("10000"))
    data object `상품2` : OrderItemFixture(productItemId = 2L, productName = "상품2", productPrice = BigDecimal("20000"))

    fun toEntity(order: Order, productItemId: Long = this.productItemId): OrderItem =
        OrderItem(order, productItemId, productName, productPrice, quantity)
}
