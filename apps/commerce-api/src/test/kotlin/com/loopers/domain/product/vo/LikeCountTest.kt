package com.loopers.domain.product.vo

import com.loopers.support.error.CoreException
import com.loopers.support.error.ErrorType
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class LikeCountTest {

    @Nested
    inner class `좋아요 수를 생성할 때` {

        @Test
        fun `0으로 생성할 수 있다`() {
            // given
            val value = 0L

            // when
            val likeCount = LikeCount(value)

            // then
            assertThat(likeCount.value).isEqualTo(0L)
        }

        @Test
        fun `양수로 생성할 수 있다`() {
            // given
            val value = 1L

            // when
            val likeCount = LikeCount(value)

            // then
            assertThat(likeCount.value).isEqualTo(1L)
        }

        @Test
        fun `음수로 생성하면 CoreException REQUIRED_ZERO_OR_POSITIVE_PRODUCT_LIKE_COUNT 예외를 던진다`() {
            // given
            val value = -1L

            // when & then
            assertThatThrownBy {
                LikeCount(value)
            }.isInstanceOf(CoreException::class.java)
                .extracting("errorType")
                .isEqualTo(ErrorType.REQUIRED_ZERO_OR_POSITIVE_PRODUCT_LIKE_COUNT)
        }
    }
}
