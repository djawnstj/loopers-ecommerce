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

            // when then
            assertThatThrownBy {
                LikeCount(value)
            }.isInstanceOf(CoreException::class.java)
                .extracting("errorType")
                .isEqualTo(ErrorType.REQUIRED_ZERO_OR_POSITIVE_PRODUCT_LIKE_COUNT)
        }
    }

    @Nested
    inner class `좋아요 수를 증가시킬 때` {

        @Test
        fun `inc() 호출 시 1이 증가한다`() {
            // given
            val likeCount = LikeCount(5L)

            // when
            val result = likeCount.inc()

            // then
            assertThat(result.value).isEqualTo(6L)
        }

        @Test
        fun `++ 연산자로 증가시킬 수 있다`() {
            // given
            var likeCount = LikeCount(10L)

            // when
            likeCount++

            // then
            assertThat(likeCount.value).isEqualTo(11L)
        }
    }

    @Nested
    inner class `좋아요 수를 차감시킬 때` {

        @Test
        fun `dec() 호출 시 1이 증가한다`() {
            // given
            val likeCount = LikeCount(5L)

            // when
            val result = likeCount.dec()

            // then
            assertThat(result.value).isEqualTo(4L)
        }

        @Test
        fun `-- 연산자로 증가시킬 수 있다`() {
            // given
            var likeCount = LikeCount(10L)

            // when
            likeCount--

            // then
            assertThat(likeCount.value).isEqualTo(9L)
        }
    }

    @Nested
    inner class `좋아요 수가 0인지 확인할 때` {
        @Test
        fun `좋아요 값이 0 이면 true 를 반환 한다`() {
            // given
            val likeCount = LikeCount.ZERO

            // when
            val actual = likeCount.isZero()

            // then
            assertThat(actual).isTrue()
        }

        @Test
        fun `좋아요 값이 0 이 아니면 false 를 반환 한다`() {
            // given
            val likeCount = LikeCount(1)

            // when
            val actual = likeCount.isZero()

            // then
            assertThat(actual).isFalse()
        }
    }
}
