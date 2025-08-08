package com.loopers.fixture.coupon

import com.loopers.domain.coupon.Coupon
import com.loopers.domain.coupon.vo.CouponType
import java.math.BigDecimal

sealed class CouponFixture(
    val name: String = "5000원 할인 쿠폰",
    val quantity: Long = 100L,
    val discount: BigDecimal = BigDecimal(5000.0),
    val type: CouponType = CouponType.FIXED,
) {

    data object `고정 할인 5000원 쿠폰` : CouponFixture(
        name = "5000원 할인 쿠폰",
        quantity = 100L,
        discount = BigDecimal(5000.0),
        type = CouponType.FIXED,
    )

    data object `퍼센트 할인 10% 쿠폰` : CouponFixture(
        name = "10% 할인 쿠폰",
        quantity = 50L,
        discount = BigDecimal(10.0),
        type = CouponType.PERCENT,
    )

    fun toEntity(): Coupon = Coupon(
        name = name,
        quantity = quantity,
        discount = discount,
        type = type,
    )
}
