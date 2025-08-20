package com.loopers.application.payment.command

import com.loopers.domain.payment.vo.PaymentType
import java.math.BigDecimal

data class ExecutePayCommand(
    val loginId: Long,
    val paymentType: PaymentType,
    val orderNumber: String,
    val amount: BigDecimal,
)
