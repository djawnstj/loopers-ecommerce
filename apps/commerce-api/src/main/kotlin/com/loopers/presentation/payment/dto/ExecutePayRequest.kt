package com.loopers.presentation.payment.dto

import com.loopers.application.payment.command.ExecutePayCommand
import com.loopers.domain.payment.vo.PaymentType

data class ExecutePayRequest(
    val paymentType: PaymentType,
    val orderNumber: String,
    val card: ExecutePayCommand.CardInfo?,
) {
    fun toCommand(loginId: String): ExecutePayCommand = ExecutePayCommand(loginId, paymentType, orderNumber, card)
}
