package com.loopers.domain.payment.param

import com.loopers.domain.payment.Payment
import com.loopers.domain.payment.vo.PaymentStatusType
import com.loopers.domain.payment.vo.PaymentType
import java.math.BigDecimal

data class RecordPaidPaymentParam(
    val orderId: Long,
    val paymentKey: String?,
    val amount: BigDecimal,
    val type: PaymentType,
) {
    fun toEntity(): Payment = Payment(orderId, paymentKey, amount, type, PaymentStatusType.PAID)
}
