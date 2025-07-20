package com.loopers.point.application

import com.loopers.point.application.command.UserPointBalanceResult
import com.loopers.point.application.command.UserPointIncreaseResult
import com.loopers.point.domain.UserPointService
import com.loopers.user.domain.UserService
import org.springframework.stereotype.Component
import java.math.BigDecimal

@Component
class UserPointFacade(
    private val userPointService: UserPointService,
    private val userService: UserService,
) {
    fun increasePoint(loginId: String, amount: BigDecimal): UserPointIncreaseResult {
        val user = userService.getUserProfile(loginId)
        return UserPointIncreaseResult(userPointService.chargePoint(user.id, amount))
    }

    fun getPointBalance(loginId: String): UserPointBalanceResult {
        val user = userService.getUserProfile(loginId)
        val userPoint = userPointService.getUserPoint(user.id)

        return UserPointBalanceResult(userPoint.balance.value)
    }
}
