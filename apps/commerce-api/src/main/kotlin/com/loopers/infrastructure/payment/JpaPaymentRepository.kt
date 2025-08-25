package com.loopers.infrastructure.payment

import com.linecorp.kotlinjdsl.support.spring.data.jpa.repository.KotlinJdslJpqlExecutor
import com.loopers.domain.payment.Payment
import org.springframework.data.jpa.repository.JpaRepository

interface JpaPaymentRepository : JpaRepository<Payment, Long>, KotlinJdslJpqlExecutor {
    fun findByPaymentKey(paymentKey: String): Payment?
}
