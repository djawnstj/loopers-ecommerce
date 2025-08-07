package com.loopers.domain.order.param

import com.loopers.domain.product.ProductItem
import java.math.BigDecimal

data class SubmitOrderParam(
    val userId: Long,
    val orderItems: List<OrderItem>,
    val totalAmount: BigDecimal,
    val payPrice: BigDecimal,
) {
    data class OrderItem(
        val productItemId: Long,
        val productItemName: String,
        val productItemPrice: BigDecimal,
        val quantity: Int,
    ) {
        companion object {
            operator fun invoke(productItem: ProductItem, quantity: Int): OrderItem =
                OrderItem(
                    productItem.id,
                    productItem.name,
                    productItem.price.value,
                    quantity,
                )
        }
    }
}
