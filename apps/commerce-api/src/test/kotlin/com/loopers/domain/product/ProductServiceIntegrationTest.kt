package com.loopers.domain.product

import com.loopers.domain.product.params.GetProductParam
import com.loopers.domain.product.vo.ProductStatusType
import com.loopers.fixture.product.ProductFixture
import com.loopers.infrastructure.product.JpaProductRepository
import com.loopers.support.IntegrationTestSupport
import com.loopers.support.enums.sort.ProductSortType
import com.loopers.support.error.CoreException
import com.loopers.support.error.ErrorType
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.assertj.core.groups.Tuple
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.time.LocalDateTime

class ProductServiceIntegrationTest(
    private val cut: ProductService,
    private val jpaProductRepository: JpaProductRepository,
) : IntegrationTestSupport() {

    @Nested
    inner class `상품 목록을 조회할 때` {

        @Test
        fun `활성 상태인 상품만 조회된다`() {
            // given
            val activeProduct1 = ProductFixture.`활성 상품 1`.toEntity()
            val activeProduct2 = ProductFixture.`활성 상품 2`.toEntity()
            val inactiveProduct = ProductFixture.`비활성 상품`.toEntity()

            jpaProductRepository.saveAll(listOf(activeProduct1, activeProduct2, inactiveProduct))

            val param = GetProductParam(
                brandId = null,
                sortType = null,
                page = 0,
                perPage = 10,
            )

            // when
            val result = cut.getProducts(param)

            // then
            assertThat(result).hasSize(2)
                .extracting("name", "status")
                .containsExactlyInAnyOrder(
                    Tuple.tuple("활성상품 1", ProductStatusType.ACTIVE),
                    Tuple.tuple("활성상품 2", ProductStatusType.ACTIVE),
                )
        }

        @Test
        fun `정렬 조건이 LATEST일 때 판매 시작일 기준 최신순으로 조회된다`() {
            // given
            val product1 = ProductFixture.`25년 1월 1일 판매 상품`.toEntity()
            val product2 = ProductFixture.`25년 1월 2일 판매 상품`.toEntity()

            jpaProductRepository.saveAll(listOf(product1, product2))

            val param = GetProductParam(
                brandId = null,
                sortType = ProductSortType.LATEST,
                page = 0,
                perPage = 10,
            )

            // when
            val result = cut.getProducts(param)

            // then
            assertThat(result).hasSize(2)
                .extracting("saleStartAt")
                .containsExactly(
                    LocalDateTime.parse("2025-01-02T00:00:00"),
                    LocalDateTime.parse("2025-01-01T00:00:00"),
                )
        }

        @Test
        fun `정렬 조건이 없을 때 ID 역순으로 조회된다`() {
            // given
            val product1 = ProductFixture.`활성 상품 1`.toEntity()
            val product2 = ProductFixture.`활성 상품 2`.toEntity()

            jpaProductRepository.saveAllAndFlush(listOf(product1, product2))

            val param = GetProductParam(
                brandId = null,
                sortType = null,
                page = 0,
                perPage = 10,
            )

            // when
            val result = cut.getProducts(param)

            // then
            assertThat(result).hasSize(2)
                .extracting("id")
                .containsExactly(2L, 1L)
        }

        @Test
        fun `페이징이 정상적으로 적용된다`() {
            // given
            val products = (1..5).map {
                ProductFixture.create(name = "상품 $it")
            }

            jpaProductRepository.saveAllAndFlush(products)

            val param = GetProductParam(
                brandId = null,
                sortType = null,
                page = 1,
                perPage = 2,
            )

            // when
            val result = cut.getProducts(param)

            // then
            assertThat(result).hasSize(2)
                .extracting("name")
                .containsExactly("상품 4", "상품 3")
        }

        @Test
        fun `빈 목록이 반환되어도 정상적으로 처리한다`() {
            // given
            val param = GetProductParam(
                brandId = null,
                sortType = ProductSortType.LATEST,
                page = 0,
                perPage = 10,
            )

            // when
            val result = cut.getProducts(param)

            // then
            assertThat(result).isEmpty()
        }

        @Test
        fun `brandId로 필터링하여 조회할 수 있다`() {
            // given
            val brand1Product1 = ProductFixture.create(name = "브랜드1 상품1", brandId = 1L)
            val brand1Product2 = ProductFixture.create(name = "브랜드1 상품2", brandId = 1L)
            val brand2Product1 = ProductFixture.create(name = "브랜드2 상품1", brandId = 2L)

            jpaProductRepository.saveAllAndFlush(listOf(brand1Product1, brand1Product2, brand2Product1))

            val param = GetProductParam(
                brandId = 1L,
                sortType = null,
                page = 0,
                perPage = 10,
            )

            // when
            val result = cut.getProducts(param)

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

            jpaProductRepository.saveAllAndFlush(listOf(brand1Product, brand2Product))

            val param = GetProductParam(
                brandId = null,
                sortType = null,
                page = 0,
                perPage = 10,
            )

            // when
            val result = cut.getProducts(param)

            // then
            assertThat(result).hasSize(2)
                .extracting("name", "brandId")
                .containsExactlyInAnyOrder(Tuple.tuple("브랜드1 상품", 1L), Tuple.tuple("브랜드2 상품", 2L))
        }

        @Test
        fun `삭제되지 않은 상품만 조회된다`() {
            // given
            val activeProduct = ProductFixture.`활성 상품 1`.toEntity()
            val deletedProduct = ProductFixture.create(name = "삭제된상품").apply { delete() }

            jpaProductRepository.saveAllAndFlush(listOf(activeProduct, deletedProduct))

            val param = GetProductParam(
                brandId = null,
                sortType = null,
                page = 0,
                perPage = 10,
            )

            // when
            val result = cut.getProducts(param)

            // then
            assertThat(result).hasSize(1)
                .extracting("deletedAt")
                .containsExactly(null)
        }
    }

    @Nested
    inner class `상품 상세 정보를 조회할 때` {

        @Test
        fun `존재하는 상품 ID로 조회하면 해당 상품을 반환한다`() {
            // given
            val product = ProductFixture.`활성 상품 1`.toEntity()
            val savedProduct = jpaProductRepository.saveAndFlush(product)

            // when
            val result = cut.getActiveProductDetail(savedProduct.id)

            // then
            assertThat(result).isNotNull
                .extracting("name")
                .isEqualTo("활성상품 1")
        }

        @Test
        fun `존재하지 않는 상품 ID로 조회하면 CoreException PRODUCT_NOT_FOUND 예외를 던진다`() {
            // given
            val nonExistentId = 999L

            // when & then
            assertThatThrownBy {
                cut.getActiveProductDetail(nonExistentId)
            }.isInstanceOf(CoreException::class.java)
                .extracting("errorType", "message")
                .containsExactly(
                    ErrorType.PRODUCT_NOT_FOUND,
                    "상품 식별자가 999 에 해당하는 상품 정보를 찾지 못했습니다.",
                )
        }

        @Test
        fun `삭제된 상품 ID로 조회하면 CoreException PRODUCT_NOT_FOUND 예외를 던진다`() {
            // given
            val product = ProductFixture.`활성 상품 1`.toEntity().apply { delete() }
            val savedProduct = jpaProductRepository.saveAndFlush(product)

            // when & then
            assertThatThrownBy {
                cut.getActiveProductDetail(savedProduct.id)
            }.isInstanceOf(CoreException::class.java)
                .extracting("errorType", "message")
                .containsExactly(
                    ErrorType.PRODUCT_NOT_FOUND,
                    "상품 식별자가 ${savedProduct.id} 에 해당하는 상품 정보를 찾지 못했습니다.",
                )
        }
    }
}
