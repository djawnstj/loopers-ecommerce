package com.loopers.infrastructure.product

import com.loopers.domain.product.ProductItem
import com.loopers.domain.product.ProductItemRepository
import com.loopers.fixture.product.ProductFixture
import com.loopers.fixture.product.ProductItemFixture
import com.loopers.support.IntegrationTestSupport
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class ProductItemRepositoryImplTest(
    private val cut: ProductItemRepository,
    private val jpaProductRepository: JpaProductRepository,
    private val jpaProductItemRepository: JpaProductItemRepository,
) : IntegrationTestSupport() {

    @Nested
    inner class `ID 목록으로 상품 아이템 목록을 조회할 때` {

        @Test
        fun `존재하는 ID 목록으로 조회하면 해당 상품 아이템들을 반환한다`() {
            // given
            val product = ProductFixture.`활성 상품 1`.toEntity()
            val savedProduct = jpaProductRepository.saveAndFlush(product)

            val productItem1 = ProductItemFixture.`검은색 라지 만원`.toEntity(savedProduct)
            val productItem2 = ProductItemFixture.`빨간색 라지 만원`.toEntity(savedProduct)
            val savedItems = jpaProductItemRepository.saveAllAndFlush(listOf(productItem1, productItem2))

            val requestIds = savedItems.map(ProductItem::id)

            // when
            val actual = cut.findAllByIds(requestIds)

            // then
            assertThat(actual).hasSize(2)
                .extracting("name")
                .containsExactlyInAnyOrder("검은색 라지", "빨간색 라지")
        }

        @Test
        fun `존재하지 않는 ID가 포함된 목록으로 조회하면 존재하는 상품 아이템만 반환한다`() {
            // given
            val product = ProductFixture.`활성 상품 1`.toEntity()
            val savedProduct = jpaProductRepository.saveAndFlush(product)

            val productItem = ProductItemFixture.`검은색 라지 만원`.toEntity(savedProduct)
            val savedItem = jpaProductItemRepository.saveAndFlush(productItem)

            val requestIds = listOf(savedItem.id, 2L)

            // when
            val actual = cut.findAllByIds(requestIds)

            // then
            assertThat(actual).hasSize(1)
                .extracting("name")
                .containsExactly("검은색 라지")
        }

        @Test
        fun `삭제된 상품 아이템 ID가 포함된 목록으로 조회하면 삭제되지 않은 상품 아이템만 반환한다`() {
            // given
            val product = ProductFixture.`활성 상품 1`.toEntity()
            val savedProduct = jpaProductRepository.saveAndFlush(product)

            val activeItem = ProductItemFixture.`검은색 라지 만원`.toEntity(savedProduct)
            val deletedItem = ProductItemFixture.`빨간색 라지 만원`.toEntity(savedProduct).also(ProductItem::delete)
            val savedItems = jpaProductItemRepository.saveAllAndFlush(listOf(activeItem, deletedItem))

            val requestIds = savedItems.map(ProductItem::id)

            // when
            val actual = cut.findAllByIds(requestIds)

            // then
            assertThat(actual).hasSize(1)
                .extracting("name")
                .containsExactly("검은색 라지")
        }

        @Test
        fun `빈 ID 목록으로 조회하면 빈 리스트를 반환한다`() {
            // given
            val product = ProductFixture.`활성 상품 1`.toEntity()
            val savedProduct = jpaProductRepository.saveAndFlush(product)

            val productItem1 = ProductItemFixture.`검은색 라지 만원`.toEntity(savedProduct)
            val productItem2 = ProductItemFixture.`빨간색 라지 만원`.toEntity(savedProduct)
            jpaProductItemRepository.saveAllAndFlush(listOf(productItem1, productItem2))

            val emptyIds = emptyList<Long>()

            // when
            val actual = cut.findAllByIds(emptyIds)

            // then
            assertThat(actual).isEmpty()
        }

        @Test
        fun `조회된 상품 아이템에는 Product가 fetch join으로 함께 조회된다`() {
            // given
            val product = ProductFixture.`활성 상품 1`.toEntity()
            val savedProduct = jpaProductRepository.saveAndFlush(product)

            val productItem = ProductItemFixture.`검은색 라지 만원`.toEntity(savedProduct)
            val savedItem = jpaProductItemRepository.saveAndFlush(productItem)

            val requestIds = listOf(savedItem.id)

            // when
            val actual = cut.findAllByIds(requestIds)

            // then
            assertThat(actual).hasSize(1)
                .extracting("product.name")
                .containsExactly(
                    "활성상품 1",
                )
        }
    }
}
