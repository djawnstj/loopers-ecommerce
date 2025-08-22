package com.loopers.presentation.payment

import com.loopers.application.payment.PaymentFacade
import com.loopers.presentation.auth.LoginId
import com.loopers.presentation.payment.dto.ExecutePayRequest
import com.loopers.presentation.payment.dto.HandlePaymentHookRequest
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class PaymentV1Controller(
    private val paymentFacade: PaymentFacade,
) {

    @PostMapping("/api/v1/payments")
    fun executePay(
        @LoginId loginId: String,
        request: ExecutePayRequest
    ) {
        paymentFacade.executePay(request.toCommand(loginId))
    }

    @PostMapping("/api/v1/payments/hooks")
    fun handlePaymentHook(request: HandlePaymentHookRequest) {
        paymentFacade.handlePaymentHook(request.toCommand())
    }
}
