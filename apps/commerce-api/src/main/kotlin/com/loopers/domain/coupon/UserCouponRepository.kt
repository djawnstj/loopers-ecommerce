package com.loopers.domain.coupon

interface UserCouponRepository {
    fun findByUserIdAndCouponIdWithOptimisticLock(userId: Long, couponId: Long): UserCoupon?
}
