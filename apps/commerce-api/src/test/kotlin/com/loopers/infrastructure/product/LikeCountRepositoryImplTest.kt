package com.loopers.infrastructure.product

import com.loopers.domain.product.ProductLikeCount
import com.loopers.domain.product.ProductLikeCountRepository
import com.loopers.fixture.product.ProductLikeCountFixture
import com.loopers.support.IntegrationTestSupport
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class LikeCountRepositoryImplTest(
    private val cut: ProductLikeCountRepository,
    private val jpaRepository: JpaProductLikeCountRepository,
) : IntegrationTestSupport() {

    @Nested
    inner class `상품 ID로 좋아요 수를 조회할 때` {

        @Test
        fun `존재하는 상품 ID로 조회하면 해당 좋아요 수를 반환한다`() {
            // given
            val productLikeCount = ProductLikeCountFixture.`좋아요 10개`.toEntity()
            val savedLikeCount = jpaRepository.saveAndFlush(productLikeCount)

            // when
            val actual = cut.findByProductIdWithOptimisticLock(savedLikeCount.productId)

            // then
            assertThat(actual).isNotNull
                .extracting("productId", "count")
                .containsExactly(1L, 10L)
        }

        @Test
        fun `존재하지 않는 상품 ID로 조회하면 null을 반환한다`() {
            // given
            val nonExistentProductId = 999L

            // when
            val actual = cut.findByProductIdWithOptimisticLock(nonExistentProductId)

            // then
            assertThat(actual).isNull()
        }

        @Test
        fun `삭제된 좋아요 수 데이터는 조회되지 않는다`() {
            // given
            val productLikeCount = ProductLikeCountFixture.`좋아요 10개`.toEntity().also(ProductLikeCount::delete)
            val savedLikeCount = jpaRepository.saveAndFlush(productLikeCount)

            // when
            val actual = cut.findByProductIdWithOptimisticLock(savedLikeCount.productId)

            // then
            assertThat(actual).isNull()
        }
    }
}
