package com.loopers.infrastructure.payment.fake

import com.loopers.domain.payment.Payment
import com.loopers.domain.payment.PaymentRepository

class TestPaymentRepository : PaymentRepository {
    private val payments = mutableListOf<Payment>()

    override fun save(payment: Payment): Payment {
        payments.add(payment)
        return payment
    }

    fun clear() {
        payments.clear()
    }

    fun findAll(): List<Payment> = payments.toList()
}