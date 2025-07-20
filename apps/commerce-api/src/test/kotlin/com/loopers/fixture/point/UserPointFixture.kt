package com.loopers.fixture.point

import com.loopers.point.domain.UserPoint
import com.loopers.point.domain.vo.Point
import java.math.BigDecimal

sealed class UserPointFixture(
    val userId: Long = 1,
    val balance: BigDecimal = BigDecimal.ZERO,
) {
    data object `기본` : UserPointFixture()
    data object `0 포인트` : UserPointFixture()
    data object `1 포인트` : UserPointFixture(balance = BigDecimal.ONE)
    data object `1000 포인트` : UserPointFixture(balance = BigDecimal(1000))
    data object `음수 포인트` : UserPointFixture(balance = BigDecimal(-0.1))
    data object `양수 포인트` : UserPointFixture(balance = BigDecimal(0.1))

    fun toEntity(userId: Long = this.userId): UserPoint = UserPoint(userId, Point(balance))
}
