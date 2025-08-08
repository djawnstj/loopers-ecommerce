package com.loopers.domain.coupon

import com.loopers.domain.BaseEntity
import com.loopers.domain.coupon.vo.CouponType
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Table
import java.math.BigDecimal

@Entity
@Table(name = "coupon")
open class Coupon(
    name: String,
    quantity: Long,
    discount: BigDecimal,
    type: CouponType,
) : BaseEntity() {
    var name: String = name
        protected set
    var quantity: Long = quantity
        protected set
    var discount: BigDecimal = discount
        protected set

    @Enumerated(EnumType.STRING)
    var type: CouponType = type
        protected set

    fun calculateDiscount(orderAmount: BigDecimal): BigDecimal = when (type) {
        CouponType.FIXED -> maxOf(orderAmount - discount, BigDecimal.ZERO)
        CouponType.PERCENT -> maxOf(orderAmount - (orderAmount * (discount.divide(PERCENTAGE))))
    }

    companion object {
        private val PERCENTAGE = BigDecimal(100)
    }
}
