package com.loopers.application.product

import com.loopers.application.product.command.GetProductRankingCommand
import com.loopers.application.product.command.GetProductRankingResult
import com.loopers.application.product.command.GetProductsCommand
import com.loopers.cache.SortedCacheRepository
import com.loopers.domain.brand.Brand
import com.loopers.domain.product.Product
import com.loopers.domain.product.ProductLikeCount
import com.loopers.domain.product.RankingPeriod
import com.loopers.domain.product.cache.ProductCacheKey
import com.loopers.domain.product.mv.MvProductRankWeekly
import com.loopers.domain.product.mv.MvProductRankMonthly
import com.loopers.domain.product.vo.ProductStatusType
import com.loopers.fixture.brand.BrandFixture
import com.loopers.fixture.product.ProductFixture
import com.loopers.fixture.product.ProductLikeCountFixture
import com.loopers.infrastructure.brand.JpaBrandRepository
import com.loopers.infrastructure.product.JpaProductLikeCountRepository
import com.loopers.infrastructure.product.JpaProductRepository
import com.loopers.infrastructure.product.mv.JpaMvProductRankWeeklyRepository
import com.loopers.infrastructure.product.mv.JpaMvProductRankMonthlyRepository
import com.loopers.support.IntegrationTestSupport
import com.loopers.support.error.CoreException
import com.loopers.support.error.ErrorType
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.assertj.core.groups.Tuple
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.data.domain.PageRequest
import java.time.LocalDate
import java.time.LocalDateTime

class ProductFacadeIntegrationTest(
    private val cut: ProductFacade,
    private val jpaProductRepository: JpaProductRepository,
    private val jpaBrandRepository: JpaBrandRepository,
    private val jpaProductLikeCountRepository: JpaProductLikeCountRepository,
    private val sortedCacheRepository: SortedCacheRepository,
    private val jpaMvProductRankWeeklyRepository: JpaMvProductRankWeeklyRepository,
    private val jpaMvProductRankMonthlyRepository: JpaMvProductRankMonthlyRepository,
) : IntegrationTestSupport() {

    @Test
    fun `상품 목록을 조회할 수 있다`() {
        // given
        val activeProduct1 = ProductFixture.`활성 상품 1`.toEntity()
        val activeProduct2 = ProductFixture.`활성 상품 2`.toEntity()
        val inactiveProduct = ProductFixture.`비활성 상품`.toEntity()

        jpaProductRepository.saveAll(listOf(activeProduct1, activeProduct2, inactiveProduct))

        val command = GetProductsCommand(
            brandId = null,
            sortType = null,
            page = 0,
            perPage = 10,
        )

        // when
        val actual = cut.getProducts(command)

        // then
        assertThat(actual.products).hasSize(2)
            .extracting("id", "name")
            .containsExactlyInAnyOrder(
                Tuple.tuple(1L, "활성상품 1"),
                Tuple.tuple(2L, "활성상품 2"),
            )
    }

    @Nested
    inner class `상품 상세 정보를 조회할 때` {

        @Test
        fun `존재하지 않는 상품 ID로 조회하면 CoreException PRODUCT_NOT_FOUND 예외를 던진다`() {
            // given
            val nonExistentId = 999L

            // when then
            assertThatThrownBy {
                cut.getProductDetail(nonExistentId)
            }.isInstanceOf(CoreException::class.java)
                .extracting("errorType", "message")
                .containsExactly(
                    ErrorType.PRODUCT_ITEM_NOT_FOUND,
                    "식별자가 999 에 해당하는 상품 정보를 찾지 못했습니다.",
                )
        }

        @Test
        fun `존재하지 않는 브랜드 ID를 가진 상품으로 조회하면 CoreException BRAND_NOT_FOUND 예외를 던진다`() {
            // given
            val product = jpaProductRepository.saveAndFlush(
                ProductFixture.create(name = "테스트상품", brandId = 999L),
            )

            // when then
            assertThatThrownBy {
                cut.getProductDetail(product.id)
            }.isInstanceOf(CoreException::class.java)
                .extracting("errorType", "message")
                .containsExactly(
                    ErrorType.BRAND_NOT_FOUND,
                    "식별자가 999 에 해당하는 브랜드 정보를 찾지 못했습니다.",
                )
        }

        @Test
        fun `삭제된 상품 ID로 조회하면 CoreException PRODUCT_NOT_FOUND 예외를 던진다`() {
            // given
            val product = jpaProductRepository.saveAndFlush(
                ProductFixture.`활성 상품 1`.toEntity().also(Product::delete),
            )

            // when then
            assertThatThrownBy {
                cut.getProductDetail(product.id)
            }.isInstanceOf(CoreException::class.java)
                .extracting("errorType", "message")
                .containsExactly(
                    ErrorType.PRODUCT_ITEM_NOT_FOUND,
                    "식별자가 ${product.id} 에 해당하는 상품 정보를 찾지 못했습니다.",
                )
        }

        @Test
        fun `삭제된 브랜드 ID를 가진 상품으로 조회하면 CoreException BRAND_NOT_FOUND 예외를 던진다`() {
            // given
            val brand = jpaBrandRepository.saveAndFlush(
                BrandFixture.`활성 브랜드`.toEntity().also(Brand::delete),
            )
            val product = jpaProductRepository.saveAndFlush(
                ProductFixture.create(name = "테스트상품", brandId = brand.id),
            )

            // when then
            assertThatThrownBy {
                cut.getProductDetail(product.id)
            }.isInstanceOf(CoreException::class.java)
                .extracting("errorType", "message")
                .containsExactly(
                    ErrorType.BRAND_NOT_FOUND,
                    "식별자가 ${brand.id} 에 해당하는 브랜드 정보를 찾지 못했습니다.",
                )
        }

        @Test
        fun `존재하는 상품 ID로 조회하면 상품 상세 정보를 반환한다`() {
            // given
            val brand = jpaBrandRepository.saveAndFlush(BrandFixture.`활성 브랜드`.toEntity())
            val product = jpaProductRepository.saveAndFlush(
                ProductFixture.create(
                    name = "활성상품 1",
                    brandId = brand.id,
                    saleStartAt = LocalDateTime.parse("2025-01-01T00:00:00"),
                ),
            )
            jpaProductLikeCountRepository.saveAndFlush(ProductLikeCountFixture.`좋아요 10개`.toEntity(productId = product.id))

            // when
            val actual = cut.getProductDetail(product.id)

            // then
            assertThat(actual)
                .extracting(
                    "name",
                    "saleStartAt",
                    "status",
                    "brandName",
                    "likeCount",
                    "rank",
                ).containsExactly(
                    "활성상품 1",
                    LocalDateTime.parse("2025-01-01T00:00:00"),
                    ProductStatusType.ACTIVE,
                    "활성브랜드",
                    0L,
                    null,
                )
        }

        @Test
        fun `상품에 랭킹 정보가 있으면 순위와 함께 상세 정보를 반환한다`() {
            // given
            val brand = jpaBrandRepository.saveAndFlush(BrandFixture.`활성 브랜드`.toEntity())
            val product = jpaProductRepository.saveAndFlush(
                ProductFixture.create(
                    name = "인기상품",
                    brandId = brand.id,
                    saleStartAt = LocalDateTime.parse("2025-01-01T00:00:00"),
                ),
            )

            val today = LocalDate.now()
            val cacheKey = ProductCacheKey.ProductRankingPerDays(today)

            sortedCacheRepository.save(cacheKey, 100.0, product.id)
            sortedCacheRepository.save(cacheKey, 90.0, 101L)
            sortedCacheRepository.save(cacheKey, 80.0, 102L)
            sortedCacheRepository.save(cacheKey, 70.0, 103L)
            sortedCacheRepository.save(cacheKey, 60.0, 104L)

            // when
            val actual = cut.getProductDetail(product.id)

            // then
            assertThat(actual)
                .extracting("name", "rank")
                .containsExactly("인기상품", 1L)
        }

        @Test
        fun `좋아요 수 정보가 없으면 0으로 설정된다`() {
            // given
            val brand = jpaBrandRepository.saveAndFlush(BrandFixture.`활성 브랜드`.toEntity())
            val product = jpaProductRepository.saveAndFlush(
                ProductFixture.create(name = "활성상품 1", brandId = brand.id),
            )

            // when
            val actual = cut.getProductDetail(product.id)

            // then
            assertThat(actual.likeCount).isEqualTo(0L)
        }

        @Test
        fun `삭제된 좋아요 수 정보는 0으로 설정된다`() {
            // given
            val brand = jpaBrandRepository.saveAndFlush(BrandFixture.`활성 브랜드`.toEntity())
            val product = jpaProductRepository.saveAndFlush(
                ProductFixture.create(name = "활성상품 1", brandId = brand.id),
            )
            jpaProductLikeCountRepository.saveAndFlush(
                ProductLikeCountFixture.`좋아요 10개`.toEntity(productId = product.id).also(ProductLikeCount::delete),
            )

            // when
            val actual = cut.getProductDetail(product.id)

            // then
            assertThat(actual.likeCount).isEqualTo(0L)
        }
    }

    @Nested
    inner class `기간별 상품 랭킹을 조회할 때` {

        @Test
        fun `일간 랭킹을 조회할 수 있다`() {
            // given
            val brand = jpaBrandRepository.saveAndFlush(BrandFixture.`활성 브랜드`.toEntity())
            val product1 = jpaProductRepository.saveAndFlush(
                ProductFixture.create(name = "일간1등상품", brandId = brand.id),
            )
            val product2 = jpaProductRepository.saveAndFlush(
                ProductFixture.create(name = "일간2등상품", brandId = brand.id),
            )

            val today = LocalDate.now()
            val cacheKey = ProductCacheKey.ProductRankingPerDays(today)

            sortedCacheRepository.save(cacheKey, 100.0, product1.id)
            sortedCacheRepository.save(cacheKey, 90.0, product2.id)

            val command = GetProductRankingCommand(
                date = today,
                pageable = PageRequest.of(0, 10),
                period = RankingPeriod.DAILY
            )

            // when
            val actual = cut.getProductRankings(command)

            // then
            assertThat(actual)
                .extracting("rankings", "totalCount")
                .containsExactly(
                    listOf(
                        GetProductRankingResult.RankedProduct(productId = product2.id, productName = "일간2등상품", brandId = brand.id, likeCount = 0, rank = 1),
                        GetProductRankingResult.RankedProduct(productId = product1.id, productName = "일간1등상품", brandId = brand.id, likeCount = 0, rank = 2),
                    ),
                    2L
                )
        }

        @Test
        fun `주간 랭킹을 조회할 수 있다`() {
            // given
            val brand = jpaBrandRepository.saveAndFlush(BrandFixture.`활성 브랜드`.toEntity())
            val product1 = jpaProductRepository.saveAndFlush(
                ProductFixture.create(name = "주간1등상품", brandId = brand.id),
            )
            val product2 = jpaProductRepository.saveAndFlush(
                ProductFixture.create(name = "주간2등상품", brandId = brand.id),
            )

            val today = LocalDate.now()
            jpaMvProductRankWeeklyRepository.saveAllAndFlush(listOf(
                MvProductRankWeekly(
                    productId = product1.id,
                    productName = "주간1등상품",
                    brandId = brand.id,
                    likeCount = 0L,
                    rank = 1L
                ),
                MvProductRankWeekly(
                    productId = product2.id,
                    productName = "주간2등상품",
                    brandId = brand.id,
                    likeCount = 0L,
                    rank = 2L
                )
            ))

            val command = GetProductRankingCommand(
                date = today,
                pageable = PageRequest.of(0, 10),
                period = RankingPeriod.WEEKLY
            )

            // when
            val actual = cut.getProductRankings(command)

            // then
            assertThat(actual)
                .extracting("rankings", "totalCount")
                .containsExactly(
                    listOf(
                        GetProductRankingResult.RankedProduct(productId = product1.id, productName = "주간1등상품", brandId = brand.id, likeCount = 0L, rank = 2),
                        GetProductRankingResult.RankedProduct(productId = product2.id, productName = "주간2등상품", brandId = brand.id, likeCount = 0L, rank = 3),
                    ),
                    2L
                )
        }

        @Test
        fun `월간 랭킹을 조회할 수 있다`() {
            // given
            val brand = jpaBrandRepository.saveAndFlush(BrandFixture.`활성 브랜드`.toEntity())
            val product1 = jpaProductRepository.saveAndFlush(
                ProductFixture.create(name = "월간1등상품", brandId = brand.id),
            )
            val product2 = jpaProductRepository.saveAndFlush(
                ProductFixture.create(name = "월간2등상품", brandId = brand.id),
            )

            val today = LocalDate.now()
            jpaMvProductRankMonthlyRepository.saveAllAndFlush(listOf(
                MvProductRankMonthly(
                    productId = product1.id,
                    productName = "월간1등상품",
                    brandId = brand.id,
                    likeCount = 0L,
                    rank = 1L
                ),
                MvProductRankMonthly(
                    productId = product2.id,
                    productName = "월간2등상품",
                    brandId = brand.id,
                    likeCount = 0L,
                    rank = 2L
                )
            ))

            val command = GetProductRankingCommand(
                date = today,
                pageable = PageRequest.of(0, 10),
                period = RankingPeriod.MONTHLY
            )

            // when
            val actual = cut.getProductRankings(command)

            // then
            assertThat(actual)
                .extracting("rankings", "totalCount")
                .containsExactly(
                    listOf(
                        GetProductRankingResult.RankedProduct(productId = product1.id, productName = "월간1등상품", brandId = brand.id, likeCount = 0L, rank = 2),
                        GetProductRankingResult.RankedProduct(productId = product2.id, productName = "월간2등상품", brandId = brand.id, likeCount = 0L, rank = 3),
                    ),
                    2L
                )
        }
    }
}
