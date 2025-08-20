package com.loopers.domain.payment.param

import com.loopers.domain.payment.Payment
import com.loopers.domain.payment.vo.PaymentStatusType
import com.loopers.domain.payment.vo.PaymentType
import java.math.BigDecimal

data class RecordFailedPaymentParam(
    val orderId: Long,
    val amount: BigDecimal,
    val type: PaymentType,
) {
    fun toEntity(): Payment = Payment(orderId, amount, type, PaymentStatusType.FAILED)
}
