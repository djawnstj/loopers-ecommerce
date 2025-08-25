package com.loopers.fixture.payment

import com.loopers.domain.payment.Payment
import com.loopers.domain.payment.vo.PaymentStatusType
import com.loopers.domain.payment.vo.PaymentType
import java.math.BigDecimal

sealed class PaymentFixture(
    val orderId: Long = 1L,
    val paymentKey: String = "paymentKey",
    val amount: BigDecimal = BigDecimal("10000"),
    val type: PaymentType = PaymentType.CARD,
    val status: PaymentStatusType = PaymentStatusType.PAID,
) {
    data object `1만원 카드 결제 성공` : PaymentFixture()
    data object `1만원 포인트 결제 성공` : PaymentFixture(type = PaymentType.POINT)

    fun toEntity(orderId: Long = this.orderId): Payment = Payment(orderId, paymentKey, amount, type, status)
}
