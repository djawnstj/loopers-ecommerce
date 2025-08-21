package com.loopers.presentation.payment.dto

import com.loopers.application.payment.processor.command.HandlePaymentHookCommand
import com.loopers.support.enums.payment.CardType

data class HandlePaymentHookRequest(
    val transactionKey: String,
    val orderId: String,
    val cardType: CardType,
    val cardNo: String,
    val amount: Long,
    val status: String,
    val reason: String?,
) {
    fun toCommand(): HandlePaymentHookCommand =
        HandlePaymentHookCommand(
            transactionKey,
            orderId,
            cardType,
            cardNo,
            amount,
            HandlePaymentHookCommand.TransactionStatus.valueOf(status),
            reason,
        )
}
