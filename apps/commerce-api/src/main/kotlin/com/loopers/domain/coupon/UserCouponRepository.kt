package com.loopers.domain.coupon

interface UserCouponRepository {
    fun findByUserIdAndCouponIdWithOptimisticLock(userId: Long, couponId: Long): UserCoupon?
    fun update(userCoupon: UserCoupon): UserCoupon
}
