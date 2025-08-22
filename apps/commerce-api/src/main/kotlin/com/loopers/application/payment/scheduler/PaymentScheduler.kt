package com.loopers.application.payment.scheduler

import com.loopers.domain.order.OrderService
import com.loopers.domain.payment.PaymentService
import com.loopers.domain.payment.param.UpsertPaymentParam
import com.loopers.domain.payment.vo.PaymentStatusType
import com.loopers.domain.payment.vo.PaymentType
import com.loopers.infrastructure.payment.client.PaymentClientFacade
import com.loopers.infrastructure.payment.client.TransactionResponse
import com.loopers.infrastructure.payment.client.TransactionStatusResponse
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.math.BigDecimal

@Component
class PaymentScheduler(
    private val orderService: OrderService,
    private val paymentService: PaymentService,
    private val paymentClientFacade: PaymentClientFacade,
) {

    @Scheduled(fixedDelayString = "\${payment.pg-status-check.interval:30000}")
    fun checkPaymentStatus() {
        orderService.getPendingOrders()
            .forEach { order ->
                paymentClientFacade.getPaymentsByOrderId(order.orderNumber)
                    .doOnNext { res -> upsertPayment(res.transactions, order.id, order.payPrice.value) }
                    .subscribe()
            }
    }

    private fun upsertPayment(
        transactions: List<TransactionResponse>,
        orderId: Long,
        amount: BigDecimal,
    ) {
        transactions.onEach { payment ->
            paymentService.upsertPaymentByPaymentKey(
                UpsertPaymentParam(
                    orderId,
                    payment.transactionKey,
                    amount,
                    PaymentType.CARD,
                    PaymentStatusType.fromPgStatus(payment.status),
                ),
            )
        }.lastOrNull()?.let {
            if (it.status == TransactionStatusResponse.FAILED) {
                orderService.cancelOrder(orderId)
            }
        }
    }
}
