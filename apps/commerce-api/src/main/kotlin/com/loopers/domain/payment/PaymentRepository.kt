package com.loopers.domain.payment

interface PaymentRepository {
    fun save(payment: Payment): Payment
}
