package com.loopers.application.order

import com.loopers.application.order.command.CreateOrderCommand
import com.loopers.domain.order.OrderService
import com.loopers.domain.point.UserPointService
import com.loopers.domain.product.ProductService
import com.loopers.domain.user.UserService
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class OrderFacade(
    private val userService: UserService,
    private val productService: ProductService,
    private val orderService: OrderService,
    private val userPointService: UserPointService,
) {

    @Transactional
    fun createOrder(command: CreateOrderCommand) {
        val user = userService.getUserProfile(command.loginId)
        val productItems =
            productService.getProductItemsDetail(command.orderItemSummaries.map(CreateOrderCommand.OrderItemSummary::productItemId))

        val order = orderService.submitOrder(command.toSubmitOrderParam(user.id, productItems))

        userPointService.useUserPoint(user.id, order.payPrice.value)

        productService.deductProductItemsQuantity(command.toDeductProductItemsQuantityParam())
    }
}
