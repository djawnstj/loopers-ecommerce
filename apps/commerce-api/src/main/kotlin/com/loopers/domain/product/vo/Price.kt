package com.loopers.domain.product.vo

import com.loopers.support.error.CoreException
import com.loopers.support.error.ErrorType
import java.math.BigDecimal

@JvmInline
value class Price(val value: BigDecimal) {
    init {
        if (value < BigDecimal.ZERO) {
            throw CoreException(ErrorType.INVALID_PRICE_VALUE)
        }
    }
}
