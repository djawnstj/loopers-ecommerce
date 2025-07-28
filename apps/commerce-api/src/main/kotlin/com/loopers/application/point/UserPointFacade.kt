package com.loopers.application.point

import com.loopers.application.point.command.UserPointBalanceResult
import com.loopers.application.point.command.UserPointIncreaseResult
import com.loopers.domain.point.UserPointService
import com.loopers.domain.user.UserService
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
