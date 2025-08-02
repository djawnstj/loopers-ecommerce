package com.loopers.domain.product.vo

import com.loopers.support.error.CoreException
import com.loopers.support.error.ErrorType

@JvmInline
value class Quantity(val value: Int) {
    init {
        if (value < 0) {
            throw CoreException(ErrorType.INVALID_QUANTITY_VALUE)
        }
    }
}
