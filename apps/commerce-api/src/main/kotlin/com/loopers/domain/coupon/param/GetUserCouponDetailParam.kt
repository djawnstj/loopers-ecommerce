package com.loopers.domain.coupon.param

import java.math.BigDecimal

data class GetUserCouponDetailParam(
    val userId: Long,
    val couponId: Long?,
    val totalAmount: BigDecimal,
)
