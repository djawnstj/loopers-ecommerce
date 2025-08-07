package com.loopers.domain.coupon

import com.loopers.domain.BaseEntity
import com.loopers.support.error.CoreException
import com.loopers.support.error.ErrorType
import jakarta.persistence.Entity
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import jakarta.persistence.Version
import java.math.BigDecimal
import java.time.Clock
import java.time.LocalDateTime

@Entity
@Table(name = "user_coupon")
class UserCoupon(
    coupon: Coupon,
    userId: Long,
    issuedAt: LocalDateTime,
    usedAt: LocalDateTime? = null,
) : BaseEntity() {

    @ManyToOne
    @JoinColumn(name = "coupon_id")
    var coupon: Coupon = coupon
        protected set
    var userId: Long = userId
        protected set
    var issuedAt: LocalDateTime = issuedAt
        protected set
    var usedAt: LocalDateTime? = usedAt
        protected set

    @Version
    var version: Long = 0
        protected set

    fun use(orderAmount: BigDecimal, clock: Clock = Clock.systemDefaultZone()): BigDecimal {
        if (usedAt != null) {
            throw CoreException(ErrorType.ALREADY_USED_USER_COUPON)
        }

        this.usedAt = LocalDateTime.now(clock)

        return coupon.calculateDiscount(orderAmount)
    }
}
