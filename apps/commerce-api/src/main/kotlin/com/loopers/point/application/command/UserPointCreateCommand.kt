package com.loopers.point.application.command

import com.loopers.point.domain.UserPoint
import java.math.BigDecimal

data class UserPointCreateResult(
    val userId: Long,
    val balance: BigDecimal,
) {
    companion object {
        operator fun invoke(userPoint: UserPoint): UserPointCreateResult =
            UserPointCreateResult(userPoint.userId, userPoint.balance.value)
    }
}
