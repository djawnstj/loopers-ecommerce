package com.loopers.application.payment.processor

import com.loopers.application.payment.command.ProcessCardPayCommand
import com.loopers.application.payment.command.ProcessPayCommand
import com.loopers.domain.order.Order
import com.loopers.domain.order.OrderService
import com.loopers.domain.payment.PaymentService
import com.loopers.domain.payment.vo.PaymentType
import com.loopers.infrastructure.payment.client.PaymentClientFacade
import org.springframework.stereotype.Component

@Component
class CardPaymentProcessor(
    private val orderService: OrderService,
    private val paymentService: PaymentService,
    private val client: PaymentClientFacade,
) : PaymentProcessor {
    override fun support(paymentType: PaymentType): Boolean = (paymentType == PaymentType.CARD)

    override fun process(command: ProcessPayCommand) {
        (command as ProcessCardPayCommand).let {
            val order: Order = orderService.getOrderByOrderNumber(command.orderNumber)
            val amount = order.payPrice.value

            client.processPayment(it.userId, it.toPaymentRequest(order.orderNumber, amount))
                .doOnNext { res ->
                    paymentService.recordPendingPayment(
                        command.toRecordPendingPaymentParam(order.id, res.transactionKey, amount),
                    )
                }.doOnError {

                }.subscribe()

            orderService.pendingOrder(order.id)
        }
    }
}
