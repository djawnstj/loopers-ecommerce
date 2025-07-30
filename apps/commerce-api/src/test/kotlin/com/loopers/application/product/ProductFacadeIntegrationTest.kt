package com.loopers.application.product

import com.loopers.application.product.command.GetProductCommand
import com.loopers.domain.product.vo.ProductStatusType
import com.loopers.fixture.product.ProductFixture
import com.loopers.infrastructure.product.JpaProductRepository
import com.loopers.support.IntegrationTestSupport
import com.loopers.support.enums.sort.ProductSortType
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.groups.Tuple
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.time.LocalDateTime

class ProductFacadeIntegrationTest(
    private val cut: ProductFacade,
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

            val command = GetProductCommand(
                brandId = null,
                sortType = null,
                page = 0,
                perPage = 10,
            )

            // when
            val result = cut.getProducts(command)

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

            val command = GetProductCommand(
                brandId = null,
                sortType = ProductSortType.LATEST,
                page = 0,
                perPage = 10,
            )

            // when
            val result = cut.getProducts(command)

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

            val command = GetProductCommand(
                brandId = null,
                sortType = null,
                page = 0,
                perPage = 10,
            )

            // when
            val result = cut.getProducts(command)

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

            val command = GetProductCommand(
                brandId = null,
                sortType = null,
                page = 1,
                perPage = 2,
            )

            // when
            val result = cut.getProducts(command)

            // then
            assertThat(result).hasSize(2)
                .extracting("name")
                .containsExactly("상품 4", "상품 3")
        }
    }
}
