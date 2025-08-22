package com.loopers.domain.payment.param

import com.loopers.domain.payment.vo.PaymentStatusType
import com.loopers.domain.payment.vo.PaymentType
import java.math.BigDecimal

data class UpsertPaymentParam(
    val orderId: Long,
    val paymentKey: String,
    val amount: BigDecimal,
    val type: PaymentType,
    val status: PaymentStatusType,
)
