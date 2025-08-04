package com.loopers.presentation.order.dto

import com.loopers.application.order.command.CreateOrderCommand

data class CreateOrderRequest(
    val orderItemSummaries: List<OrderItemSummary>,
) {
    fun toCommand(loginId: String): CreateOrderCommand =
        CreateOrderCommand(loginId, orderItemSummaries.map { CreateOrderCommand.OrderItemSummary(it.productItemId, it.quantity) })

    data class OrderItemSummary(val productItemId: Long, val quantity: Int)
}

data class CreateOrderResponse(val success: Boolean = true)
