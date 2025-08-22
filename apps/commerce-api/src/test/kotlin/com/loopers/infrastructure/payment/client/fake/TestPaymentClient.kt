package com.loopers.infrastructure.payment.client.fake

import com.loopers.infrastructure.payment.client.OrderResponse
import com.loopers.infrastructure.payment.client.PaymentClient
import com.loopers.infrastructure.payment.client.PaymentRequest
import com.loopers.infrastructure.payment.client.PaymentResponse
import com.loopers.infrastructure.payment.client.TransactionDetailResponse
import com.loopers.infrastructure.payment.client.TransactionStatusResponse
import reactor.core.publisher.Mono

class TestPaymentClient : PaymentClient {
    private val payments = mutableMapOf<String, PaymentResponse>()
    private var shouldFail = false
    private var failureReason = "Payment failed"

    override fun processPayment(request: PaymentRequest, userId: Long): Mono<PaymentResponse> {
        return if (shouldFail) {
            Mono.error(RuntimeException(failureReason))
        } else {
            val response = PaymentResponse(
                transactionKey = "txn_${System.currentTimeMillis()}",
                status = TransactionStatusResponse.FAILED,
                reason = null,
            )
            payments[response.transactionKey] = response
            Mono.just(response)
        }
    }

    override fun getPayment(transactionKey: String, userId: Long): Mono<TransactionDetailResponse> {
        TODO()
    }

    override fun getPaymentsByOrderId(orderId: String, userId: Long): Mono<OrderResponse> {
        TODO()
    }

    fun setFailure(shouldFail: Boolean, reason: String = "Payment failed") {
        this.shouldFail = shouldFail
        this.failureReason = reason
    }

    fun getPayments(): Collection<PaymentResponse> = payments.values

    fun clear() {
        payments.clear()
        shouldFail = false
        failureReason = "Payment failed"
    }
}
