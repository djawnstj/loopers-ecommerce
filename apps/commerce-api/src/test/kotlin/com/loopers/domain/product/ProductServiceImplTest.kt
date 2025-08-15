package com.loopers.domain.product

import com.loopers.domain.product.params.DeductProductItemsQuantityParam
import com.loopers.domain.product.params.GetProductParam
import com.loopers.domain.product.vo.LikeCount
import com.loopers.domain.product.vo.ProductStatusType
import com.loopers.fixture.brand.BrandFixture
import com.loopers.fixture.product.ProductFixture
import com.loopers.fixture.product.ProductItemFixture
import com.loopers.fixture.product.ProductLikeCountFixture
import com.loopers.infrastructure.product.fake.TestProductLikeCountRepository
import com.loopers.infrastructure.product.fake.TestProductRepository
import com.loopers.support.error.CoreException
import com.loopers.support.error.ErrorType
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.assertj.core.groups.Tuple
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import java.time.LocalDateTime

class ProductServiceImplTest {

    @Nested
    inner class `상품 목록을 조회할 때` {

        @Test
        fun `조회 결과가 없다면 빈 목록을 반환 한다`() {
            // given
            val productRepository = TestProductRepository()
            val productLikeCountRepository = TestProductLikeCountRepository()
            val cut = ProductServiceImpl(productRepository)

            val param = GetProductParam(null, null, 0, 10)

            // when
            val actual = cut.getProducts(param)

            // then
            assertThat(actual).isEmpty()
        }

        @Test
        fun `조회 결과를 반환 한다`() {
            // given
            val productRepository = TestProductRepository()
            val productLikeCountRepository = TestProductLikeCountRepository()
            val cut = ProductServiceImpl(productRepository)

            val product1 = ProductFixture.`활성 상품 1`.toEntity()
            val product2 = ProductFixture.`활성 상품 2`.toEntity()

            productRepository.saveAll(listOf(product1, product2))

            val param = GetProductParam(null, null, 0, 10)

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
    }

    @Nested
    inner class `상품 상세 정보를 조회할 때` {

        @Test
        fun `존재하는 상품 ID로 조회하면 해당 상품을 반환 한다`() {
            // given
            val productRepository = TestProductRepository()
            val productLikeCountRepository = TestProductLikeCountRepository()
            val cut = ProductServiceImpl(productRepository)

            val product = ProductFixture.`활성 상품 1`.toEntity()
            val savedProduct = productRepository.save(product)

            // when
            val actual = cut.getActiveProductInfo(savedProduct.id)

            // then
            assertThat(actual).isNotNull
        }

        @Test
        fun `존재하지 않는 상품 ID로 조회하면 CoreException PRODUCT_ITEM_NOT_FOUND 예외를 던진다`() {
            // given
            val productRepository = TestProductRepository()
            val productLikeCountRepository = TestProductLikeCountRepository()
            val cut = ProductServiceImpl(productRepository)

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
    }

    @Nested
    inner class `상품 상세 정보를 집계할 때` {

        @Test
        fun `상품 정보와 브랜드 정보를 결합하여 ProductDetailView를 반환한다`() {
            // given
            val productRepository = TestProductRepository()
            val productLikeCountRepository = TestProductLikeCountRepository()
            val cut = ProductServiceImpl(productRepository)

            val product = ProductFixture.`활성 상품 1`.toEntity()
            val savedProduct = productRepository.save(product)

            val brand = BrandFixture.`활성 브랜드`.toEntity()

            val productLikeCount = ProductLikeCountFixture.`좋아요 10개`.toEntity()
            productLikeCountRepository.save(productLikeCount)

            // when
            val actual = cut.aggregateProductDetail(savedProduct, brand)

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
            val productRepository = TestProductRepository()
            val productLikeCountRepository = TestProductLikeCountRepository()
            val cut = ProductServiceImpl(productRepository)

            val product = ProductFixture.`활성 상품 1`.toEntity()
            val savedProduct = productRepository.save(product)

            val brand = BrandFixture.`활성 브랜드`.toEntity()

            // when
            val actual = cut.aggregateProductDetail(savedProduct, brand)

            // then
            assertThat(actual.likeCount).isEqualTo(LikeCount.ZERO)
        }

        @Test
        fun `삭제된 좋아요 수 정보는 0으로 설정된다`() {
            // given
            val productRepository = TestProductRepository()
            val productLikeCountRepository = TestProductLikeCountRepository()
            val cut = ProductServiceImpl(productRepository)

            val product = ProductFixture.`활성 상품 1`.toEntity()
            val savedProduct = productRepository.save(product)

            val brand = BrandFixture.`활성 브랜드`.toEntity()

            val productLikeCount = ProductLikeCountFixture.`좋아요 10개`.toEntity().also(ProductLikeCount::delete)
            productLikeCountRepository.save(productLikeCount)

            // when
            val actual = cut.aggregateProductDetail(savedProduct, brand)

            // then
            assertThat(actual.likeCount).isEqualTo(LikeCount.ZERO)
        }
    }

    @Nested
    inner class `상품 아이템 상세 정보를 조회할 때` {

        @Test
        fun `존재하는 상품 아이템 ID들로 조회하면 해당 상품 아이템들을 반환한다`() {
            // given
            val productRepository = TestProductRepository()
            val productLikeCountRepository = TestProductLikeCountRepository()
            val cut = ProductServiceImpl(productRepository)

            val product = ProductFixture.`활성 상품 1`.toEntity()
            val savedProduct = productRepository.save(product)

            val productItem1 = ProductItemFixture.`검은색 라지 만원`.toEntity(savedProduct)
            val productItem2 = ProductItemFixture.`빨간색 라지 만원`.toEntity(savedProduct)
            val savedProductItem1 = productRepository.saveProductItem(productItem1)
            val savedProductItem2 = productRepository.saveProductItem(productItem2)

            val productItemIds = listOf(savedProductItem1.id, savedProductItem2.id)

            // when
            val actual = cut.getProductItemsDetailWithLock(productItemIds)

            // then
            assertThat(actual).hasSize(2)
                .extracting("name", "price")
                .containsExactlyInAnyOrder(
                    Tuple.tuple("검은색 라지", java.math.BigDecimal("10000")),
                    Tuple.tuple("빨간색 라지", java.math.BigDecimal("10000")),
                )
        }

        @Test
        fun `일부 상품 아이템 ID가 존재하지 않으면 CoreException PRODUCT_ITEM_NOT_FOUND 예외를 던진다`() {
            // given
            val productRepository = TestProductRepository()
            val productLikeCountRepository = TestProductLikeCountRepository()
            val cut = ProductServiceImpl(productRepository)

            val product = ProductFixture.`활성 상품 1`.toEntity()
            val savedProduct = productRepository.save(product)

            val productItem = ProductItemFixture.`검은색 라지 만원`.toEntity(savedProduct)
            val savedProductItem = productRepository.saveProductItem(productItem)

            val productItemIds = listOf(savedProductItem.id, 999L)

            // when then
            assertThatThrownBy {
                cut.getProductItemsDetailWithLock(productItemIds)
            }.isInstanceOf(CoreException::class.java)
                .extracting("errorType", "message")
                .containsExactly(
                    ErrorType.PRODUCT_ITEM_NOT_FOUND,
                    "일부 상품 아이템을 찾을 수 없습니다.",
                )
        }
    }

    @Nested
    inner class `상품 아이템 수량을 차감할 때` {

        @Test
        fun `존재하지 않는 상품 아이템 ID가 포함되면 CoreException PRODUCT_ITEM_NOT_FOUND 예외를 던진다`() {
            // given
            val productRepository = TestProductRepository()
            val productLikeCountRepository = TestProductLikeCountRepository()
            val cut = ProductServiceImpl(productRepository)

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
        fun `존재하는 상품 아이템들의 수량을 정상적으로 차감한다`() {
            // given
            val productRepository = TestProductRepository()
            val productLikeCountRepository = TestProductLikeCountRepository()
            val cut = ProductServiceImpl(productRepository)

            val product = ProductFixture.`활성 상품 1`.toEntity()
            val savedProduct = productRepository.save(product)

            val savedProductItem1 = productRepository.saveProductItem(ProductItemFixture.`검은색 라지 만원`.toEntity(savedProduct))
            val savedProductItem2 = productRepository.saveProductItem(ProductItemFixture.`빨간색 라지 만원`.toEntity(savedProduct))

            val param = DeductProductItemsQuantityParam(
                listOf(
                    DeductProductItemsQuantityParam.DeductItem(savedProductItem1.id, 3),
                    DeductProductItemsQuantityParam.DeductItem(savedProductItem2.id, 5),
                ),
            )

            // when
            cut.deductProductItemsQuantity(param)

            // then
            val actual = productRepository.findProductItemsByIds(listOf(savedProductItem1.id, savedProductItem2.id))
            assertThat(actual).extracting("name", "quantity")
                .containsExactly(
                    Tuple.tuple("검은색 라지", 7),
                    Tuple.tuple("빨간색 라지", 5),
                )
        }
    }

    @Nested
    inner class `상품 좋아요 수를 증가시킬 때` {

        @Test
        fun `좋아요 대상이 되는 상품이 없다면 CoreException PRODUCT_NOT_FOUND 예외를 던진다`() {
            // given
            val productRepository = TestProductRepository()
            val productLikeCountRepository = TestProductLikeCountRepository()
            val cut = ProductServiceImpl(productRepository)

            // when then
            assertThatThrownBy {
                cut.increaseProductLikeCount(1L)
            }.isInstanceOf(CoreException::class.java)
                .extracting("errorType", "message")
                .containsExactly(ErrorType.PRODUCT_NOT_FOUND, "식별자가 1 에 해당하는 상품 정보를 찾지 못했습니다.")
        }

        @Test
        fun `기존 좋아요 수가 있으면 1 증가시킨다`() {
            // given
            val productRepository = TestProductRepository()
            val cut = ProductServiceImpl(productRepository)

            val product = ProductFixture.`활성 상품 1`.toEntity()
            val savedProduct = productRepository.save(product)

            // when
            cut.increaseProductLikeCount(savedProduct.id)

            // then
            assertThat(product.likeCount.value).isEqualTo(1L)
        }
    }

    @Nested
    inner class `상품 좋아요 수를 차감시킬 때` {

        @Test
        fun `좋아요 대상이 되는 상품이 없다면 CoreException PRODUCT_NOT_FOUND 예외를 던진다`() {
            // given
            val productRepository = TestProductRepository()
            val productLikeCountRepository = TestProductLikeCountRepository()
            val cut = ProductServiceImpl(productRepository)

            // when then
            assertThatThrownBy {
                cut.decreaseProductLikeCount(1L)
            }.isInstanceOf(CoreException::class.java)
                .extracting("errorType", "message")
                .containsExactly(ErrorType.PRODUCT_NOT_FOUND, "식별자가 1 에 해당하는 상품 정보를 찾지 못했습니다.")
        }

        @Test
        fun `기존 좋아요 수가 있으면 1 차감시킨다`() {
            // given
            val productRepository = TestProductRepository()
            val cut = ProductServiceImpl(productRepository)

            val product = ProductFixture.`활성 상품 1`.toEntity()
            product.increaseLikeCount()
            val savedProduct = productRepository.save(product)

            // when
            cut.decreaseProductLikeCount(savedProduct.id)

            // then
            assertThat(product.likeCount.value).isEqualTo(0L)
        }
    }
}
