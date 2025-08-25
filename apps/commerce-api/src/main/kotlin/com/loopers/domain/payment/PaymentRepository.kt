package com.loopers.domain.payment

interface PaymentRepository {
    fun save(payment: Payment): Payment
    fun findByPaymentKey(paymentKey: String): Payment?
}
