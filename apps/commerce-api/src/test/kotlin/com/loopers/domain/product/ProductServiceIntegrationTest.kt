package com.loopers.domain.product

import com.loopers.domain.product.params.DeductProductItemsQuantityParam
import com.loopers.domain.product.params.GetProductParam
import com.loopers.domain.product.vo.LikeCount
import com.loopers.domain.product.vo.ProductStatusType
import com.loopers.domain.product.vo.Quantity
import com.loopers.fixture.brand.BrandFixture
import com.loopers.fixture.product.ProductFixture
import com.loopers.fixture.product.ProductItemFixture
import com.loopers.infrastructure.brand.JpaBrandRepository
import com.loopers.infrastructure.product.JpaProductItemRepository
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
import org.springframework.data.repository.findByIdOrNull
import java.time.LocalDateTime
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class ProductServiceIntegrationTest(
    private val cut: ProductService,
    private val jpaProductRepository: JpaProductRepository,
    private val jpaBrandRepository: JpaBrandRepository,
    private val jpaProductItemRepository: JpaProductItemRepository,
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
            val actual = cut.getProducts(param)

            // then
            assertThat(actual).hasSize(2)
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

            jpaProductRepository.saveAllAndFlush(listOf(brand1Product1, brand1Product2, brand2Product1))

            val param = GetProductParam(
                brandId = 1L,
                sortType = null,
                page = 0,
                perPage = 10,
            )

            // when
            val actual = cut.getProducts(param)

            // then
            assertThat(actual).hasSize(2)
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
            val actual = cut.getProducts(param)

            // then
            assertThat(actual).hasSize(2)
                .extracting("name", "brandId")
                .containsExactlyInAnyOrder(Tuple.tuple("브랜드1 상품", 1L), Tuple.tuple("브랜드2 상품", 2L))
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
            val actual = cut.getProducts(param)

            // then
            assertThat(actual).hasSize(2)
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
            val actual = cut.getProducts(param)

            // then
            assertThat(actual).hasSize(2)
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
            val actual = cut.getProducts(param)

            // then
            assertThat(actual).hasSize(2)
                .extracting("name")
                .containsExactly("상품 4", "상품 3")
        }

        @Test
        fun `삭제되지 않은 상품만 조회된다`() {
            // given
            val activeProduct = ProductFixture.`활성 상품 1`.toEntity()
            val deletedProduct = ProductFixture.create(name = "삭제된상품").also(Product::delete)

            jpaProductRepository.saveAllAndFlush(listOf(activeProduct, deletedProduct))

            val param = GetProductParam(
                brandId = null,
                sortType = null,
                page = 0,
                perPage = 10,
            )

            // when
            val actual = cut.getProducts(param)

            // then
            assertThat(actual).hasSize(1)
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
            val actual = cut.getActiveProductInfo(savedProduct.id)

            // then
            assertThat(actual).isNotNull
                .extracting("name")
                .isEqualTo("활성상품 1")
        }

        @Test
        fun `존재하지 않는 상품 ID로 조회하면 CoreException PRODUCT_NOT_FOUND 예외를 던진다`() {
            // given
            val nonExistentId = 999L

            // when then
            assertThatThrownBy {
                cut.getActiveProductInfo(nonExistentId)
            }.isInstanceOf(CoreException::class.java)
                .extracting("errorType", "message")
                .containsExactly(
                    ErrorType.PRODUCT_ITEM_NOT_FOUND,
                    "식별자가 999 에 해당하는 상품 정보를 찾지 못했습니다.",
                )
        }

        @Test
        fun `삭제된 상품 ID로 조회하면 CoreException PRODUCT_NOT_FOUND 예외를 던진다`() {
            // given
            val product = ProductFixture.`활성 상품 1`.toEntity().also(Product::delete)
            val savedProduct = jpaProductRepository.saveAndFlush(product)

            // when then
            assertThatThrownBy {
                cut.getActiveProductInfo(savedProduct.id)
            }.isInstanceOf(CoreException::class.java)
                .extracting("errorType", "message")
                .containsExactly(
                    ErrorType.PRODUCT_ITEM_NOT_FOUND,
                    "식별자가 ${savedProduct.id} 에 해당하는 상품 정보를 찾지 못했습니다.",
                )
        }
    }

    @Nested
    inner class `상품 상세 정보를 집계할 때` {

        @Test
        fun `상품 정보와 브랜드 정보를 결합하여 ProductDetailView를 반환한다`() {
            // given
            val product = jpaProductRepository.saveAndFlush(ProductFixture.`활성 상품 1`.toEntity())
            val brand = jpaBrandRepository.saveAndFlush(BrandFixture.`활성 브랜드`.toEntity())

            // when
            val actual = cut.aggregateProductDetail(product, brand)

            // then
            assertThat(actual)
                .extracting(
                    "name",
                    "saleStartAt",
                    "status",
                    "brandName",
                    "likeCount",
                ).containsExactly(
                    "활성상품 1",
                    LocalDateTime.parse("2025-01-01T00:00:00"),
                    ProductStatusType.ACTIVE,
                    "활성브랜드",
                    0L,
                )
        }

        @Test
        fun `좋아요 수 정보가 없으면 0으로 설정된다`() {
            // given
            val product = jpaProductRepository.saveAndFlush(ProductFixture.`활성 상품 1`.toEntity())
            val brand = jpaBrandRepository.saveAndFlush(BrandFixture.`활성 브랜드`.toEntity())

            // when
            val actual = cut.aggregateProductDetail(product, brand)

            // then
            assertThat(actual.likeCount).isEqualTo(LikeCount.ZERO)
        }
    }

    @Nested
    inner class `상품 아이템 수량을 차감할 때` {

        @Test
        fun `존재하지 않는 상품 아이템 ID가 포함되면 CoreException PRODUCT_ITEM_NOT_FOUND 예외를 던진다`() {
            // given
            val param = DeductProductItemsQuantityParam(
                items = listOf(
                    DeductProductItemsQuantityParam.DeductItem(999L, 3),
                ),
            )

            // when then
            assertThatThrownBy {
                cut.deductProductItemsQuantity(param)
            }.isInstanceOf(CoreException::class.java)
                .extracting("errorType", "message")
                .containsExactly(
                    ErrorType.PRODUCT_ITEM_NOT_FOUND,
                    "일부 상품 아이템을 찾을 수 없습니다.",
                )
        }

        @Test
        fun `차감된 재고를 저장 한다`() {
            // given
            val product = jpaProductRepository.saveAndFlush(ProductFixture.`활성 상품 1`.toEntity())
            val productItem1 = jpaProductItemRepository.saveAndFlush(ProductItemFixture.`검은색 라지 만원`.toEntity(product))
            val productItem2 = jpaProductItemRepository.saveAndFlush(ProductItemFixture.`빨간색 라지 만원`.toEntity(product))

            val param = DeductProductItemsQuantityParam(
                listOf(
                    DeductProductItemsQuantityParam.DeductItem(productItem1.id, 3),
                    DeductProductItemsQuantityParam.DeductItem(productItem2.id, 5),
                ),
            )

            // when
            cut.deductProductItemsQuantity(param)

            // then
            val actual = jpaProductItemRepository.findAll()
            assertThat(actual).extracting("name", "quantity")
                .containsExactlyInAnyOrder(
                    Tuple.tuple("검은색 라지", 7),
                    Tuple.tuple("빨간색 라지", 5),
                )
        }

        @Test
        fun `동시에 재고 차감 요청이 들어와도 정확하게 재고 차감된다`() {
            // given
            val product = jpaProductRepository.saveAndFlush(ProductFixture.`활성 상품 1`.toEntity())
            val productItem = jpaProductItemRepository.saveAndFlush(ProductItemFixture.`재고 10개`.toEntity(product))

            val threadCount = 5
            val deductQuantityPerThread = 2
            val executor = Executors.newFixedThreadPool(threadCount)
            val latch = CountDownLatch(threadCount)

            // when
            repeat(threadCount) {
                executor.submit {
                    try {
                        val param = DeductProductItemsQuantityParam(
                            listOf(
                                DeductProductItemsQuantityParam.DeductItem(productItem.id, deductQuantityPerThread),
                            ),
                        )
                        cut.deductProductItemsQuantity(param)
                    } finally {
                        latch.countDown()
                    }
                }
            }

            latch.await(10, TimeUnit.SECONDS)
            executor.shutdown()

            // then
            val actual = jpaProductItemRepository.findByIdOrNull(productItem.id)
            assertThat(actual?.quantity).isEqualTo(Quantity(0))
        }
    }

    @Nested
    inner class `상품 좋아요 수를 증가시킬 때` {

        @Test
        fun `기존 좋아요 수가 있으면 1 증가시킨다`() {
            // given
            val product = jpaProductRepository.save(ProductFixture.`활성 상품 1`.toEntity())

            // when
            cut.increaseProductLikeCount(product.id)

            // then
            val actual = jpaProductRepository.findByIdOrNull(1L)
            assertThat(actual?.likeCount?.value).isEqualTo(1L)
        }

        @Test
        fun `좋아요 수를 증가시키면 낙관적 락 버전이 증가한다`() {
            // given
            val product = jpaProductRepository.save(ProductFixture.`활성 상품 1`.toEntity())
            val originalVersion = product.version

            // when
            cut.increaseProductLikeCount(product.id)

            // then
            val actual = jpaProductRepository.findByIdOrNull(1L)
            assertThat(actual?.version).isEqualTo(originalVersion + 1)
        }

        @Test
        fun `재시도 횟수를 초과하면 CoreException FAILED_UPDATE_PRODUCT_LIKE_COUNT 예외가 터진다`() {
            // given
            val product = jpaProductRepository.save(ProductFixture.`활성 상품 1`.toEntity())

            val threadCount = 20
            val executor = Executors.newFixedThreadPool(threadCount)
            val latch = CountDownLatch(threadCount)
            val actual = mutableListOf<Exception>()

            // when
            repeat(threadCount) {
                executor.submit {
                    try {
                        cut.increaseProductLikeCount(product.id)
                    } catch (e: Exception) {
                        synchronized(actual) {
                            actual.add(e)
                        }
                    } finally {
                        latch.countDown()
                    }
                }
            }

            latch.await()
            executor.shutdown()

            // then
            assertThat(actual).isNotEmpty
                .allMatch { it is CoreException && it.errorType == ErrorType.FAILED_UPDATE_PRODUCT_LIKE_COUNT }
        }
    }

    @Nested
    inner class `상품 좋아요 수를 차감시킬 때` {

        @Test
        fun `기존 좋아요 수가 있으면 1 차감시킨다`() {
            // given
            val product = ProductFixture.`활성 상품 1`.toEntity()
            product.increaseLikeCount()
            jpaProductRepository.save(product)

            // when
            cut.decreaseProductLikeCount(product.id)

            // then
            val actual = jpaProductRepository.findByIdOrNull(1L)
            assertThat(actual?.likeCount?.value).isEqualTo(0L)
        }

        @Test
        fun `좋아요 수를 차감시키면 낙관적 락 버전이 증가한다`() {
            // given
            val product = ProductFixture.`활성 상품 1`.toEntity()
            product.increaseLikeCount()
            jpaProductRepository.saveAndFlush(product)
            val originalVersion = product.version

            // when
            cut.decreaseProductLikeCount(product.id)

            // then
            val actual = jpaProductRepository.findByIdOrNull(1L)
            assertThat(actual?.version).isEqualTo(originalVersion + 1)
        }

        @Test
        fun `재시도 횟수를 초과하면 CoreException FAILED_UPDATE_PRODUCT_LIKE_COUNT 예외가 터진다`() {
            // given
            val product = ProductFixture.`활성 상품 1`.toEntity()
            val likeCount = 100
            repeat(likeCount) {
                product.increaseLikeCount()
            }
            jpaProductRepository.saveAndFlush(product)

            val threadCount = 20
            val executor = Executors.newFixedThreadPool(threadCount)
            val latch = CountDownLatch(threadCount)
            val actual = mutableListOf<Exception>()

            // when
            repeat(threadCount) {
                executor.submit {
                    try {
                        cut.decreaseProductLikeCount(product.id)
                    } catch (e: Exception) {
                        synchronized(actual) {
                            actual.add(e)
                        }
                    } finally {
                        latch.countDown()
                    }
                }
            }

            latch.await()
            executor.shutdown()

            // then
            assertThat(actual).isNotEmpty
                .allMatch { it is CoreException && it.errorType == ErrorType.FAILED_UPDATE_PRODUCT_LIKE_COUNT }
        }
    }
}
