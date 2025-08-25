package com.loopers.application.order

import com.loopers.application.order.command.CreateOrderCommand
import com.loopers.domain.coupon.UserCouponService
import com.loopers.domain.order.OrderService
import com.loopers.domain.product.ProductService
import com.loopers.domain.user.UserService
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class OrderFacade(
    private val userService: UserService,
    private val productService: ProductService,
    private val userCouponService: UserCouponService,
    private val orderService: OrderService,
) {

    @Transactional(timeout = 5)
    fun createOrder(command: CreateOrderCommand): String {
        val user = userService.getUserProfile(command.loginId)
        val productItems =
            productService.getProductItemsDetailWithLock(
                command.orderItemSummaries.map(CreateOrderCommand.OrderItemSummary::productItemId),
            )

        val payPrice = userCouponService.calculatePayPrice(command.toGetUserCouponDetailParam(user.id, productItems.totalAmount))

        val order = orderService.submitOrder(command.toSubmitOrderParam(user.id, productItems, payPrice))

        return order.orderNumber
    }
}
