package com.loopers.domain.payment.fake

import com.loopers.domain.payment.Payment
import com.loopers.domain.payment.PaymentService
import com.loopers.domain.payment.param.RecordFailedPaymentParam
import com.loopers.domain.payment.param.RecordPaidPaymentParam
import com.loopers.domain.payment.param.RecordPendingPaymentParam
import com.loopers.domain.payment.param.UpsertPaymentParam
import com.loopers.support.error.CoreException
import com.loopers.support.error.ErrorType

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

    override fun recordPendingPayment(param: RecordPendingPaymentParam) {
        val payment = param.toEntity()
        // Reflection을 사용하여 ID 설정
        val idField = payment.javaClass.superclass.getDeclaredField("id")
        idField.isAccessible = true
        idField.set(payment, payments.size + 1L)

        payments.add(payment)
    }

    override fun getPaymentByPaymentKey(paymentKey: String): Payment {
        return payments.find { it.paymentKey == paymentKey } ?: throw CoreException(ErrorType.PAYMENT_NOT_FOUND)
    }

    override fun upsertPaymentByPaymentKey(param: UpsertPaymentParam) {
        this.payments.find { it.paymentKey == param.paymentKey }?.updateStatus(param.status)
            ?: run {
                this.payments.add(
                    Payment(param.orderId, param.paymentKey, param.amount, param.type, param.status),
                )
            }
    }

    fun getPayments(): List<Payment> = payments.toList()

    fun getLastPayment(): Payment? = payments.lastOrNull()

    fun clear() {
        payments.clear()
    }
}
