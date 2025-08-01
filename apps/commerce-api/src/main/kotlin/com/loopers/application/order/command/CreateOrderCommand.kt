package com.loopers.application.order.command

import com.loopers.domain.order.param.SubmitOrderParam
import com.loopers.domain.product.ProductItem
import com.loopers.domain.product.params.DeductProductItemsQuantityParam

data class CreateOrderCommand(
    val loginId: String,
    val orderItemSummaries: List<OrderItemSummary>,
) {
    fun toSubmitOrderParam(userId: Long, productItems: List<ProductItem>): SubmitOrderParam = SubmitOrderParam(
        userId,
        productItems.map { productItem ->
            SubmitOrderParam.OrderItem(
                productItem,
                orderItemSummaries.first { it.productItemId == productItem.id }.quantity,
            )
        },
    )

    fun toDeductProductItemsQuantityParam(): DeductProductItemsQuantityParam =
        DeductProductItemsQuantityParam(
            orderItemSummaries.map { DeductProductItemsQuantityParam.DeductItem(it.productItemId, it.quantity) },
        )

    data class OrderItemSummary(val productItemId: Long, val quantity: Int)
}
