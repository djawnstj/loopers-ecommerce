package com.loopers.point.application

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
    fun increasePoint(userId: String, amount: BigDecimal): UserPointIncreaseResult {
        val user = userService.getUserProfile(userId)
        return UserPointIncreaseResult(userPointService.chargePoint(user.id, amount))
    }
}
