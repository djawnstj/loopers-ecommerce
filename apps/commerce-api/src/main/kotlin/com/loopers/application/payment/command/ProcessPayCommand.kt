package com.loopers.application.payment.command

import com.loopers.domain.payment.param.RecordFailedPaymentParam
import com.loopers.domain.payment.param.RecordPaidPaymentParam
import com.loopers.domain.payment.param.RecordPendingPaymentParam
import com.loopers.domain.payment.vo.PaymentType
import com.loopers.infrastructure.payment.client.PaymentRequest
import com.loopers.support.enums.payment.CardType
import java.math.BigDecimal

interface ProcessPayCommand {
    val userId: Long
    val orderNumber: String

    companion object {
        operator fun invoke(executePayCommand: ExecutePayCommand, userId: Long): ProcessPayCommand =
            when (executePayCommand.paymentType) {
                PaymentType.POINT -> ProcessPointPayCommand(userId, executePayCommand.orderNumber)

                PaymentType.CARD -> ProcessCardPayCommand(
                    userId,
                    executePayCommand.orderNumber,
                    executePayCommand.card!!.cardType,
                    executePayCommand.card.cardNo
                )
            }
    }
}

data class ProcessPointPayCommand(
    override val userId: Long,
    override val orderNumber: String,
) : ProcessPayCommand {
    fun toRecordPaidPaymentParam(orderId: Long, amount: BigDecimal): RecordPaidPaymentParam =
        RecordPaidPaymentParam(orderId, null, amount, PaymentType.POINT)

    fun toRecordFailedPaymentParam(orderId: Long, amount: BigDecimal): RecordFailedPaymentParam =
        RecordFailedPaymentParam(orderId, null, amount, PaymentType.POINT)
}

data class ProcessCardPayCommand(
    override val userId: Long,
    override val orderNumber: String,
    val cardType: CardType,
    val cardNo: String,
) : ProcessPayCommand {
    fun toPaymentRequest(orderNumber: String, amount: BigDecimal): PaymentRequest =
        PaymentRequest(
            orderNumber,
            cardType.name,
            cardNo,
            amount.toLong(),
            "http://localhost:8080/api/v1/payments/hooks",
        )

    fun toRecordPendingPaymentParam(orderId: Long, paymentKey: String, amount: BigDecimal): RecordPendingPaymentParam =
        RecordPendingPaymentParam(orderId, paymentKey, amount, PaymentType.CARD)
}
