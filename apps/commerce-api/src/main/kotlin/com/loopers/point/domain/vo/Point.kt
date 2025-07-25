package com.loopers.point.domain.vo

import com.loopers.support.error.CoreException
import com.loopers.support.error.ErrorType
import java.math.BigDecimal

@JvmInline
value class Point(val value: BigDecimal) {
    init {
        if (value < BigDecimal.ZERO) {
            throw CoreException(ErrorType.REQUIRED_ZERO_OR_POSITIVE_POINT)
        }
    }

    operator fun plus(other: Point) = Point(value + other.value)
    operator fun compareTo(other: BigDecimal): Int = value.compareTo(other)
}
