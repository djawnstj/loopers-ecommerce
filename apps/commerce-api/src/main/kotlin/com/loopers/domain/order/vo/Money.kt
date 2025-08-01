package com.loopers.domain.order.vo

import com.loopers.support.error.CoreException
import com.loopers.support.error.ErrorType
import java.math.BigDecimal

@JvmInline
value class Money(val value: BigDecimal) {
    init {
        if (value < BigDecimal.ZERO) {
            throw CoreException(ErrorType.INVALID_MONEY_VALUE)
        }
    }
}
