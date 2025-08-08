package com.loopers.application.order.command

import com.loopers.domain.coupon.param.GetUserCouponDetailParam
import com.loopers.domain.order.param.SubmitOrderParam
import com.loopers.domain.product.ProductItems
import com.loopers.domain.product.params.DeductProductItemsQuantityParam
import java.math.BigDecimal

data class CreateOrderCommand(
    val loginId: String,
    val orderItemSummaries: List<OrderItemSummary>,
    val couponId: Long? = null,
) {
    fun toGetUserCouponDetailParam(userId: Long, totalAmount: BigDecimal): GetUserCouponDetailParam =
        GetUserCouponDetailParam(userId, couponId, totalAmount)

    fun toSubmitOrderParam(userId: Long, productItems: ProductItems, payPrice: BigDecimal): SubmitOrderParam =
        SubmitOrderParam(
            userId,
            productItems.map { productItem ->
                SubmitOrderParam.OrderItem(
                    productItem,
                    orderItemSummaries.first { it.productItemId == productItem.id }.quantity,
                )
            },
            productItems.totalAmount,
            payPrice,
        )

    fun toDeductProductItemsQuantityParam(): DeductProductItemsQuantityParam =
        DeductProductItemsQuantityParam(
            orderItemSummaries.map { DeductProductItemsQuantityParam.DeductItem(it.productItemId, it.quantity) },
        )

    data class OrderItemSummary(val productItemId: Long, val quantity: Int)
}
