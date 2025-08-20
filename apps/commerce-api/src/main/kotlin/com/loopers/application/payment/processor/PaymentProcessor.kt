package com.loopers.application.payment.processor

import com.loopers.application.payment.command.ProcessPayCommand
import com.loopers.domain.payment.vo.PaymentType

interface PaymentProcessor {
    fun support(paymentType: PaymentType): Boolean
    fun process(command: ProcessPayCommand)
}

