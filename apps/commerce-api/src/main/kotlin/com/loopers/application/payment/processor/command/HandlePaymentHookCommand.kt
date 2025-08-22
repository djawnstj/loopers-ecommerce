package com.loopers.application.payment.processor.command

import com.loopers.support.enums.payment.CardType

data class HandlePaymentHookCommand(
    val transactionKey: String,
    val orderId: String,
    val cardType: CardType,
    val cardNo: String,
    val amount: Long,
    val status: TransactionStatus,
    val reason: String?,
) {
    enum class TransactionStatus {
        SUCCESS,
        FAILED,
    }
}
