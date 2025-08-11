package com.loopers.fixture.coupon

import com.loopers.domain.coupon.Coupon
import com.loopers.domain.coupon.UserCoupon
import java.time.LocalDateTime

sealed class UserCouponFixture(
    val userId: Long = 1,
    val issuedAt: LocalDateTime = LocalDateTime.parse("2025-01-01T00:00:00"),
    val usedAt: LocalDateTime? = null,
) {

    data object 기본 : UserCouponFixture()

    fun toEntity(
        coupon: Coupon = CouponFixture.`고정 할인 5000원 쿠폰`.toEntity(),
        userId: Long = this.userId,
        issuedAt: LocalDateTime = this.issuedAt,
        usedAt: LocalDateTime? = this.usedAt,
    ) = UserCoupon(
        coupon = coupon,
        userId = userId,
        issuedAt = issuedAt,
        usedAt = usedAt,
    )
}
