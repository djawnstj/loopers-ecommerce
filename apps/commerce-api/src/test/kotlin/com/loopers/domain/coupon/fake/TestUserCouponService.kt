package com.loopers.domain.coupon.fake

import com.loopers.domain.coupon.UserCoupon
import com.loopers.domain.coupon.UserCouponService
import com.loopers.domain.coupon.param.GetUserCouponDetailParam
import com.loopers.support.error.CoreException
import com.loopers.support.error.ErrorType
import java.math.BigDecimal

class TestUserCouponService : UserCouponService {
    private val userCoupons = mutableListOf<UserCoupon>()

    override fun calculatePayPrice(param: GetUserCouponDetailParam): BigDecimal {
        if (param.couponId == null) return BigDecimal.ZERO

        val userCoupon = userCoupons.find { userCoupon ->
            userCoupon.userId == param.userId &&
                    userCoupon.coupon.id == param.couponId &&
                    userCoupon.deletedAt == null
        } ?: throw CoreException(
            ErrorType.USER_COUPON_NOT_FOUND,
            "회원 식별자 ${param.userId} 회원이 가진 ${param.couponId} 쿠폰을 찾을 수 없습니다.",
        )
        return userCoupon.use(param.totalAmount)
    }

    fun addUserCoupon(userCoupon: UserCoupon) {
        userCoupons.add(userCoupon)
    }

    fun addUserCoupons(userCoupons: List<UserCoupon>) {
        this.userCoupons.addAll(userCoupons)
    }

    fun clear() {
        userCoupons.clear()
    }

    fun size(): Int = userCoupons.size

    fun findAll(): List<UserCoupon> = userCoupons.toList()
}
