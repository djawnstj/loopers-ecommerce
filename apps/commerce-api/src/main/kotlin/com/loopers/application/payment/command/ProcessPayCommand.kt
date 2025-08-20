package com.loopers.application.payment.command

import com.loopers.domain.payment.param.RecordFailedPaymentParam
import com.loopers.domain.payment.param.RecordPaidPaymentParam
import com.loopers.domain.payment.vo.PaymentType
import com.loopers.infrastructure.payment.client.PaymentRequest
import java.math.BigDecimal

interface ProcessPayCommand {
    val userId: Long
    val orderId: Long
}

data class ProcessPointPayCommand(
    override val userId: Long,
    override val orderId: Long,
) : ProcessPayCommand {
    fun toRecordPaidPaymentParam(amount: BigDecimal): RecordPaidPaymentParam =
        RecordPaidPaymentParam(orderId, amount, PaymentType.POINT)

    fun toRecordFailedPaymentParam(amount: BigDecimal): RecordFailedPaymentParam =
        RecordFailedPaymentParam(orderId, amount, PaymentType.POINT)
}

data class ProcessCardPayCommand(
    override val userId: Long,
    override val orderId: Long,
    val cardType: CardType,
    val cardNo: String,
) : ProcessPayCommand {
    fun toPaymentRequest(orderNumber: String, amount: BigDecimal): PaymentRequest =
        PaymentRequest(
            orderNumber,
            cardType.name,
            cardNo,
            amount.toPlainString(),
            "TODO",
        )

    enum class CardType {
        SAMSUNG,
        KB,
        HYUNDAI,
    }
}
