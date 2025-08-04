package com.loopers.domain.product.vo

import com.loopers.support.error.CoreException
import com.loopers.support.error.ErrorType

@JvmInline
value class LikeCount(val value: Long) {
    operator fun inc(): LikeCount = LikeCount(value + 1)
    operator fun dec(): LikeCount = LikeCount(value - 1)

    fun isZero(): Boolean = this == ZERO

    init {
        if (value < 0) {
            throw CoreException(ErrorType.REQUIRED_ZERO_OR_POSITIVE_PRODUCT_LIKE_COUNT)
        }
    }

    companion object {
        val ZERO = LikeCount(0L)
    }
}
