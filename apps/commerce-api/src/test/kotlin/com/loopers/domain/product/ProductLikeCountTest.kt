package com.loopers.domain.product

import com.loopers.domain.product.vo.LikeCount
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class ProductLikeCountTest {

    @Nested
    inner class `상품 좋아요 수를 생성할 때` {

        @Test
        fun `상품 ID와 좋아요 수로 생성할 수 있다`() {
            // given
            val productId = 1L
            val count = 5L

            // when
            val productLikeCount = ProductLikeCount(productId, count)

            // then
            assertThat(productLikeCount.productId).isEqualTo(1L)
            assertThat(productLikeCount.count).isEqualTo(LikeCount(5L))
        }

        @Test
        fun `0개의 좋아요 수로 생성할 수 있다`() {
            // given
            val productId = 1L
            val count = 0L

            // when
            val productLikeCount = ProductLikeCount(productId, count)

            // then
            assertThat(productLikeCount.productId).isEqualTo(1L)
            assertThat(productLikeCount.count).isEqualTo(LikeCount.ZERO)
        }
    }

    @Nested
    inner class `상품 좋아요 수를 증가시킬 때` {

        @Test
        fun `좋아요 수가 1 증가 시킨다`() {
            // given
            val productLikeCount = ProductLikeCount(1L, 5L)

            // when
            productLikeCount.increase()

            // then
            assertThat(productLikeCount.count.value).isEqualTo(6L)
        }
    }

    @Nested
    inner class `상품 좋아요 수를 차감시킬 때` {

        @Test
        fun `좋아요가 0인 경우 차감하지 않는다`() {
            // given
            val productLikeCount = ProductLikeCount(1L, 0L)

            // when
            productLikeCount.decrease()

            // then
            assertThat(productLikeCount.count.value).isEqualTo(0L)
        }

        @Test
        fun `좋아요 수가 1 차감 시킨다`() {
            // given
            val productLikeCount = ProductLikeCount(1L, 5L)

            // when
            productLikeCount.decrease()

            // then
            assertThat(productLikeCount.count.value).isEqualTo(4L)
        }
    }
}
