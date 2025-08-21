package com.loopers.domain.payment.fake

import com.loopers.domain.payment.Payment
import com.loopers.domain.payment.PaymentRepository

class TestPaymentRepository : PaymentRepository {
    private val payments = mutableListOf<Payment>()
    private var nextId = 1L

    override fun save(payment: Payment): Payment {
        // Reflection을 사용하여 ID 설정
        val idField = payment.javaClass.superclass.getDeclaredField("id")
        idField.isAccessible = true
        idField.set(payment, nextId++)
        
        payments.add(payment)
        return payment
    }

    override fun findByPaymentKey(paymentKey: String): Payment? {
        return payments.find { it.paymentKey == paymentKey }
    }
    
    fun findAll(): List<Payment> = payments.toList()
    
    fun clear() {
        payments.clear()
        nextId = 1L
    }
}