package com.loopers.application.payment

import com.loopers.application.payment.command.ExecutePayCommand
import com.loopers.application.payment.command.ProcessPayCommand
import com.loopers.application.payment.processor.PaymentProcessor
import com.loopers.application.payment.processor.command.HandlePaymentHookCommand
import com.loopers.domain.order.Order
import com.loopers.domain.order.OrderService
import com.loopers.domain.payment.PaymentService
import com.loopers.domain.product.ProductService
import com.loopers.domain.product.params.DeductProductItemsQuantityParam
import com.loopers.domain.user.UserService
import com.loopers.support.error.CoreException
import com.loopers.support.error.ErrorType
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class PaymentFacade(
    private val userService: UserService,
    private val paymentProcessors: List<PaymentProcessor>,
    private val paymentService: PaymentService,
    private val orderService: OrderService,
    private val productService: ProductService,
) {
    fun executePay(command: ExecutePayCommand) {
        val user = userService.getUserProfile(command.loginId)

        paymentProcessors.find { it.support(command.paymentType) }
            ?.also {
                it.process(ProcessPayCommand(command, user.id))
            } ?: throw CoreException(ErrorType.NOT_SUPPORTED_PAYMENT_TYPE)
    }

    @Transactional
    fun handlePaymentHook(command: HandlePaymentHookCommand) {
        val payment = paymentService.getPaymentByPaymentKey(command.transactionKey)
        val order = orderService.getOrderById(payment.orderId)

        when (command.status) {
            HandlePaymentHookCommand.TransactionStatus.SUCCESS -> {
                payment.paid()
                order.complete()
                productService.deductProductItemsQuantity(order.toDeductProductItemsQuantityParam())
            }

            HandlePaymentHookCommand.TransactionStatus.FAILED -> {
                payment.failed()
                order.cancel()
            }
        }
    }

    private fun Order.toDeductProductItemsQuantityParam(): DeductProductItemsQuantityParam =
        DeductProductItemsQuantityParam(orderItems.map {
            DeductProductItemsQuantityParam.DeductItem(it.productItemId, it.quantity.value)
        })
}
