package com.loopers.domain.point

import com.loopers.domain.BaseEntity
import com.loopers.domain.point.vo.Point
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

    fun deduct(amount: Point) {
        if (balance < amount) {
            throw CoreException(ErrorType.POINT_BALANCE_EXCEEDED)
        }

        balance -= amount
    }

    fun usable(amount: Point): Boolean = (balance >= amount)

    private fun Point.validateChargeable() {
        if (value <= BigDecimal.ZERO) {
            throw CoreException(ErrorType.REQUIRED_POSITIVE_POINT_CHARGE_AMOUNT)
        }
    }
}
