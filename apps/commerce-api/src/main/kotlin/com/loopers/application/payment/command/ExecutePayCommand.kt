package com.loopers.application.payment.command

import com.loopers.domain.payment.vo.PaymentType
import com.loopers.support.enums.payment.CardType

data class ExecutePayCommand(
    val loginId: String,
    val paymentType: PaymentType,
    val orderNumber: String,
    val card: CardInfo?,
) {
    data class CardInfo(val cardType: CardType, val cardNo: String)
}
