package com.loopers.point.domain

import com.loopers.domain.BaseEntity
import com.loopers.point.domain.vo.Point
import com.loopers.support.error.CoreException
import com.loopers.support.error.ErrorType
import jakarta.persistence.Entity
import jakarta.persistence.Table
import java.math.BigDecimal

@Entity
@Table(name = "user_point")
class UserPoint(
    userId: Long,
    balance: Point,
) : BaseEntity() {
    var userId: Long = userId
        protected set
    var balance: Point = balance
        protected set

    fun charge(amount: Point) {
        amount.validateChargeable()
        this.balance += amount
    }

    private fun Point.validateChargeable() {
        if (value <= BigDecimal.ZERO) {
            throw CoreException(ErrorType.REQUIRED_POSITIVE_POINT_CHARGE_AMOUNT)
        }
    }
}
