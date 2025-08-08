package com.loopers.infrastructure.coupon.fake

import com.loopers.domain.coupon.UserCoupon
import com.loopers.domain.coupon.UserCouponRepository

class TestUserCouponRepository : UserCouponRepository {
    private val userCoupons = mutableMapOf<Long, UserCoupon>()
    private var nextId = 1L

    fun clear() {
        userCoupons.clear()
        nextId = 1L
    }

    fun size(): Int = userCoupons.size

    fun findAll(): List<UserCoupon> = userCoupons.values.toList()

    fun save(userCoupon: UserCoupon): UserCoupon {
        val idField = userCoupon.javaClass.superclass.getDeclaredField("id")
        idField.isAccessible = true
        idField.set(userCoupon, nextId++)

        userCoupons[userCoupon.id] = userCoupon
        return userCoupon
    }

    override fun findByUserIdAndCouponIdWithOptimisticLock(userId: Long, couponId: Long): UserCoupon? {
        return userCoupons.values.find { userCoupon ->
            userCoupon.userId == userId &&
                    userCoupon.coupon.id == couponId &&
                    userCoupon.deletedAt == null
        }
    }

    override fun update(userCoupon: UserCoupon): UserCoupon {
        userCoupons[userCoupon.id] = userCoupon

        return userCoupon
    }
}
