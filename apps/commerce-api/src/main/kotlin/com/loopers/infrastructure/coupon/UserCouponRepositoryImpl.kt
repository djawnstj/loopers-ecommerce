package com.loopers.infrastructure.coupon

import com.loopers.domain.coupon.Coupon
import com.loopers.domain.coupon.UserCoupon
import com.loopers.domain.coupon.UserCouponRepository
import jakarta.persistence.LockModeType
import org.springframework.data.jpa.repository.Lock
import org.springframework.stereotype.Component

@Component
class UserCouponRepositoryImpl(
    private val jpaUserCouponRepository: JpaUserCouponRepository,
) : UserCouponRepository {

    @Lock(LockModeType.OPTIMISTIC)
    override fun findByUserIdAndCouponIdWithOptimisticLock(userId: Long, couponId: Long): UserCoupon? =
        jpaUserCouponRepository.findAll {
            select(
                entity(UserCoupon::class),
            ).from(
                entity(UserCoupon::class),
                fetchJoin(path(UserCoupon::coupon)),
            ).whereAnd(
                path(UserCoupon::userId).eq(userId),
                path(UserCoupon::coupon)(Coupon::id).eq(couponId),
                path(UserCoupon::deletedAt).isNull(),
            )
        }.firstOrNull()
}
