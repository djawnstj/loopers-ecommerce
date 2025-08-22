package com.loopers.infrastructure.payment

import com.loopers.domain.payment.Payment
import com.loopers.domain.payment.PaymentRepository
import org.springframework.stereotype.Repository

@Repository
class PaymentRepositoryImpl(
    private val jpaRepository: JpaPaymentRepository,
) : PaymentRepository {
    override fun save(payment: Payment): Payment = jpaRepository.save(payment)

    override fun findByPaymentKey(paymentKey: String): Payment? = jpaRepository.findByPaymentKey(paymentKey)
}
