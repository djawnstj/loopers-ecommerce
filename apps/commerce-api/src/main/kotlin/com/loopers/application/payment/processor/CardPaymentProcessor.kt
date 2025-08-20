package com.loopers.application.payment.processor

import com.loopers.application.payment.command.ProcessCardPayCommand
import com.loopers.application.payment.command.ProcessPayCommand
import com.loopers.domain.payment.vo.PaymentType
import com.loopers.infrastructure.payment.client.PaymentClient
import org.springframework.stereotype.Component

@Component
class CardPaymentProcessor(
    private val client: PaymentClient,
) : PaymentProcessor {
    override fun support(paymentType: PaymentType): Boolean = (paymentType == PaymentType.CARD)

    // TODO retry, 응답에 따른 order 상태 변화
    override fun process(command: ProcessPayCommand) {
//        (command as ProcessCardPayCommand).let {
//            client.processPayment(it.userId, it.toPaymentRequest())
//        }
    }
}
