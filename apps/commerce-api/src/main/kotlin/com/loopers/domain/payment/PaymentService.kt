package com.loopers.domain.payment

import com.loopers.domain.payment.param.RecordFailedPaymentParam
import com.loopers.domain.payment.param.RecordPaidPaymentParam
import com.loopers.domain.payment.param.RecordPendingPaymentParam
import com.loopers.support.error.CoreException
import com.loopers.support.error.ErrorType
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

interface PaymentService {
    fun recordPaidPayment(param: RecordPaidPaymentParam)
    fun recordFailedPayment(param: RecordFailedPaymentParam)
    fun recordPendingPayment(param: RecordPendingPaymentParam)
    fun getPaymentByPaymentKey(paymentKey: String): Payment
}

@Service
class PaymentServiceImpl(
    private val paymentRepository: PaymentRepository,
) : PaymentService {

    @Transactional
    override fun recordPaidPayment(param: RecordPaidPaymentParam) {
        paymentRepository.save(param.toEntity())
    }

    @Transactional
    override fun recordFailedPayment(param: RecordFailedPaymentParam) {
        paymentRepository.save(param.toEntity())
    }

    override fun recordPendingPayment(param: RecordPendingPaymentParam) {
        paymentRepository.save(param.toEntity())
    }

    override fun getPaymentByPaymentKey(paymentKey: String): Payment =
        paymentRepository.findByPaymentKey(paymentKey)
            ?: throw CoreException(ErrorType.PAYMENT_NOT_FOUND)
}
