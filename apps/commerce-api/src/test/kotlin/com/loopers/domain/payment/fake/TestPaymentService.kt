package com.loopers.domain.payment.fake

import com.loopers.domain.payment.Payment
import com.loopers.domain.payment.PaymentService
import com.loopers.domain.payment.param.RecordFailedPaymentParam
import com.loopers.domain.payment.param.RecordPaidPaymentParam

class TestPaymentService : PaymentService {
    private val payments = mutableListOf<Payment>()
    
    override fun recordPaidPayment(param: RecordPaidPaymentParam) {
        val payment = param.toEntity()
        // Reflection을 사용하여 ID 설정
        val idField = payment.javaClass.superclass.getDeclaredField("id")
        idField.isAccessible = true
        idField.set(payment, payments.size + 1L)
        
        payments.add(payment)
    }

    override fun recordFailedPayment(param: RecordFailedPaymentParam) {
        val payment = param.toEntity()
        // Reflection을 사용하여 ID 설정
        val idField = payment.javaClass.superclass.getDeclaredField("id")
        idField.isAccessible = true
        idField.set(payment, payments.size + 1L)
        
        payments.add(payment)
    }
    
    fun getPayments(): List<Payment> = payments.toList()
    
    fun getLastPayment(): Payment? = payments.lastOrNull()
    
    fun clear() {
        payments.clear()
    }
}