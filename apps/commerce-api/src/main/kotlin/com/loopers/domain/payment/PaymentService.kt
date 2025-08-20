package com.loopers.domain.payment

import com.loopers.domain.payment.param.RecordFailedPaymentParam
import com.loopers.domain.payment.param.RecordPaidPaymentParam
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

interface PaymentService {
    fun recordPaidPayment(param: RecordPaidPaymentParam)
    fun recordFailedPayment(param: RecordFailedPaymentParam)
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
}
