package com.loopers.infrastructure.product

import com.loopers.domain.product.Product
import com.loopers.domain.product.ProductRepository
import com.loopers.domain.product.vo.ProductStatusType
import com.loopers.fixture.product.ProductFixture
import com.loopers.support.IntegrationTestSupport
import com.loopers.support.enums.sort.ProductSortType
import jakarta.persistence.EntityManager
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.groups.Tuple
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.time.LocalDateTime

class ProductRepositoryImplTest(
    private val cut: ProductRepository,
    private val jpaRepository: JpaProductRepository,
) : IntegrationTestSupport() {

    @Nested
    inner class `정렬 조건으로 상품을 조회할 때` {

        @Test
        fun `정렬 조건이 없다면 id 역순으로 정렬되어 조회 된다`() {
            // given
            val product1 = ProductFixture.`25년 1월 1일 판매 상품`.toEntity()
            val product2 = ProductFixture.`25년 1월 2일 판매 상품`.toEntity()

            jpaRepository.saveAllAndFlush(listOf(product1, product2))

            // when
            val products = cut.findBySortType(brandId = null, sortBy = null, offset = 0, limit = 10)

            // then
            assertThat(products).hasSize(2)
                .extracting("id")
                .containsExactly(2L, 1L)
        }

        @Test
        fun `판매 시작일이 최신인 상품부터 정렬되어 조회할 수 있다`() {
            // given
            val product1 = ProductFixture.`25년 1월 1일 판매 상품`.toEntity()
            val product2 = ProductFixture.`25년 1월 2일 판매 상품`.toEntity()

            jpaRepository.saveAllAndFlush(listOf(product1, product2))

            // when
            val products = cut.findBySortType(brandId = null, sortBy = ProductSortType.LATEST, offset = 0, limit = 10)

            // then
            assertThat(products).hasSize(2)
                .extracting("saleStartAt")
                .containsExactly(LocalDateTime.parse("2025-01-02T00:00:00"), LocalDateTime.parse("2025-01-01T00:00:00"))
        }

        @Test
        fun `ACTIVE 상태인 상품만 조회된다`() {
            // given
            val activeProduct1 = ProductFixture.`활성 상품 1`.toEntity()
            val activeProduct2 = ProductFixture.`활성 상품 2`.toEntity()
            val inactiveProduct = ProductFixture.`비활성 상품`.toEntity()

            jpaRepository.saveAllAndFlush(listOf(activeProduct1, activeProduct2, inactiveProduct))

            // when
            val products = cut.findBySortType(brandId = null, sortBy = null, offset = 0, limit = 10)

            // then
            assertThat(products).hasSize(2)
                .extracting("name", "status")
                .containsExactlyInAnyOrder(
                    Tuple.tuple("활성상품 1", ProductStatusType.ACTIVE),
                    Tuple.tuple("활성상품 2", ProductStatusType.ACTIVE),
                )
        }

        @Test
        fun `brandId로 필터링하여 조회할 수 있다`() {
            // given
            val brand1Product1 = ProductFixture.create(name = "브랜드1 상품1", brandId = 1L)
            val brand1Product2 = ProductFixture.create(name = "브랜드1 상품2", brandId = 1L)
            val brand2Product1 = ProductFixture.create(name = "브랜드2 상품1", brandId = 2L)

            jpaRepository.saveAllAndFlush(listOf(brand1Product1, brand1Product2, brand2Product1))

            // when
            val result = cut.findBySortType(brandId = 1L, sortBy = null, offset = 0, limit = 10)

            // then
            assertThat(result).hasSize(2)
                .extracting("name", "brandId")
                .containsExactlyInAnyOrder(Tuple.tuple("브랜드1 상품1", 1L), Tuple.tuple("브랜드1 상품2", 1L))
        }

        @Test
        fun `brandId가 null이면 모든 브랜드의 상품을 조회한다`() {
            // given
            val brand1Product = ProductFixture.create(name = "브랜드1 상품", brandId = 1L)
            val brand2Product = ProductFixture.create(name = "브랜드2 상품", brandId = 2L)

            jpaRepository.saveAllAndFlush(listOf(brand1Product, brand2Product))

            // when
            val result = cut.findBySortType(brandId = null, sortBy = null, offset = 0, limit = 10)

            // then
            assertThat(result).hasSize(2)
                .extracting("name", "brandId")
                .containsExactlyInAnyOrder(Tuple.tuple("브랜드1 상품", 1L), Tuple.tuple("브랜드2 상품", 2L))
        }

        @Test
        fun `offset과 limit이 적용되어 조회 된다`() {
            // given
            val products = (1..5).map {
                ProductFixture.create(name = "상품 $it")
            }

            jpaRepository.saveAllAndFlush(products)

            // when
            val result = cut.findBySortType(brandId = null, sortBy = null, offset = 1, limit = 2)

            // then
            assertThat(result).hasSize(2)
            assertThat(result.map { it.name }).containsExactly("상품 4", "상품 3")
        }

        @Test
        fun `삭제되지 않은 상품만 조회된다`() {
            // given
            val activeProduct = ProductFixture.`활성 상품 1`.toEntity()
            val deletedProduct = ProductFixture.create(name = "삭제된상품").also(Product::delete)

            jpaRepository.saveAllAndFlush(listOf(activeProduct, deletedProduct))

            // when
            val result = cut.findBySortType(brandId = null, sortBy = null, offset = 0, limit = 10)

            // then
            assertThat(result).hasSize(1)
                .extracting("deletedAt")
                .containsExactly(null)
        }
    }

    @Nested
    inner class `ID로 상품을 조회할 때` {

        @Test
        fun `존재하는 ID로 조회하면 해당 상품을 반환 한다`() {
            // given
            val product = ProductFixture.`활성 상품 1`.toEntity()
            val savedProduct = jpaRepository.saveAndFlush(product)

            // when
            val result = cut.findActiveProductById(savedProduct.id)

            // then
            assertThat(result).isNotNull
                .extracting("name")
                .isEqualTo("활성상품 1")
        }

        @Test
        fun `존재하지 않는 ID로 조회하면 null을 반환 한다`() {
            // given
            val nonExistentId = 999L

            // when
            val result = cut.findActiveProductById(nonExistentId)

            // then
            assertThat(result).isNull()
        }

        @Test
        fun `삭제된 상품 ID로 조회하면 null을 반환 한다`() {
            // given
            val product = ProductFixture.`활성 상품 1`.toEntity().also(Product::delete)
            val savedProduct = jpaRepository.saveAndFlush(product)

            // when
            val result = cut.findActiveProductById(savedProduct.id)

            // then
            assertThat(result).isNull()
        }
    }
}
