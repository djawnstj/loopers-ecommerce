package com.loopers.application.payment

import com.loopers.application.payment.command.ExecutePayCommand
import org.springframework.stereotype.Component

@Component
class PaymentFacade {
    fun executePay(command: ExecutePayCommand) {
//        val user = userService.getUserProfile(command.loginId)
    }
}
