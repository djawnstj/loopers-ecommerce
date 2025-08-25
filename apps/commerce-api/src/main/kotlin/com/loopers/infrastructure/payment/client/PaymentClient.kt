package com.loopers.infrastructure.payment.client

import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.service.annotation.GetExchange
import org.springframework.web.service.annotation.HttpExchange
import org.springframework.web.service.annotation.PostExchange
import reactor.core.publisher.Mono

@HttpExchange(url = "/api/v1/payments", accept = ["application/json"])
interface PaymentClient {

    @PostExchange
    fun processPayment(
        @RequestBody request: PaymentRequest,
        @RequestHeader("X-USER-ID") userId: Long = 1,
    ): Mono<PaymentResponse>

    @GetExchange("/{transactionKey}")
    fun getPayment(
        @PathVariable transactionKey: String,
        @RequestHeader("X-USER-ID") userId: Long = 1,
    ): Mono<TransactionDetailResponse>

    @GetExchange
    fun getPaymentsByOrderId(
        @RequestParam orderId: String,
        @RequestHeader("X-USER-ID") userId: Long = 1,
    ): Mono<OrderResponse>
}
