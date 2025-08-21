package com.loopers.infrastructure.payment.client.fake

import com.loopers.infrastructure.payment.client.PaymentClient
import com.loopers.infrastructure.payment.client.PaymentRequest
import com.loopers.infrastructure.payment.client.PaymentResponse
import com.loopers.infrastructure.payment.client.TransactionStatusResponse
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

class TestPaymentClient : PaymentClient {
    private val payments = mutableMapOf<String, PaymentResponse>()
    private var shouldFail = false
    private var failureReason = "Payment failed"

    override fun processPayment(userId: Long, request: PaymentRequest): Mono<PaymentResponse> {
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

    override fun getPayment(userId: Long, transactionKey: String): Mono<PaymentResponse> {
        return payments[transactionKey]?.let { Mono.just(it) }
            ?: Mono.error(RuntimeException("Payment not found"))
    }

    override fun getPaymentsByOrderId(userId: Long, orderId: String): Flux<PaymentResponse> {
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
