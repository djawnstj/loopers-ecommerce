package com.loopers.domain.product

import com.loopers.domain.product.params.GetProductParam
import com.loopers.domain.product.vo.LikeCount
import com.loopers.domain.product.vo.ProductStatusType
import com.loopers.fixture.brand.BrandFixture
import com.loopers.fixture.product.ProductFixture
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
import java.time.LocalDateTime

class ProductServiceImplTest {

    @Nested
    inner class `상품 목록을 조회할 때` {

        @Test
        fun `조회 결과가 없다면 빈 목록을 반환 한다`() {
            // given
            val productRepository = TestProductRepository()
            val productLikeCountRepository = TestProductLikeCountRepository()
            val cut = ProductServiceImpl(productRepository, productLikeCountRepository)

            val param = GetProductParam(null, null, 0, 10)

            // when
            val actual =cut.getProducts(param)

            // then
            assertThat(actual).isEmpty()
        }

        @Test
        fun `조회 결과를 반환 한다`() {
            // given
            val productRepository = TestProductRepository()
            val productLikeCountRepository = TestProductLikeCountRepository()
            val cut = ProductServiceImpl(productRepository, productLikeCountRepository)

            val product1 = ProductFixture.`활성 상품 1`.toEntity()
            val product2 = ProductFixture.`활성 상품 2`.toEntity()

            productRepository.saveAll(listOf(product1, product2))

            val param = GetProductParam(null, null, 0, 10)

            // when
            val actual =cut.getProducts(param)

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
            val cut = ProductServiceImpl(productRepository, productLikeCountRepository)

            val product = ProductFixture.`활성 상품 1`.toEntity()
            val savedProduct = productRepository.save(product)

            // when
            val actual =cut.getActiveProductInfo(savedProduct.id)

            // then
            assertThat(actual).isNotNull
        }

        @Test
        fun `존재하지 않는 상품 ID로 조회하면 CoreException PRODUCT_NOT_FOUND 예외를 던진다`() {
            // given
            val productRepository = TestProductRepository()
            val productLikeCountRepository = TestProductLikeCountRepository()
            val cut = ProductServiceImpl(productRepository, productLikeCountRepository)

            val nonExistentId = 999L

            // when & then
            assertThatThrownBy {
                cut.getActiveProductInfo(nonExistentId)
            }.isInstanceOf(CoreException::class.java)
                .extracting("errorType", "message")
                .containsExactly(
                    ErrorType.PRODUCT_NOT_FOUND,
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
            val cut = ProductServiceImpl(productRepository, productLikeCountRepository)

            val product = ProductFixture.`활성 상품 1`.toEntity()
            val savedProduct = productRepository.save(product)

            val brand = BrandFixture.`활성 브랜드`.toEntity()

            val productLikeCount = ProductLikeCountFixture.`좋아요 10개`.toEntity()
            productLikeCountRepository.save(productLikeCount)

            // when
            val actual =cut.aggregateProductDetail(savedProduct, brand)

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
                    10L,
                )
        }

        @Test
        fun `좋아요 수 정보가 없으면 0으로 설정된다`() {
            // given
            val productRepository = TestProductRepository()
            val productLikeCountRepository = TestProductLikeCountRepository()
            val cut = ProductServiceImpl(productRepository, productLikeCountRepository)

            val product = ProductFixture.`활성 상품 1`.toEntity()
            val savedProduct = productRepository.save(product)

            val brand = BrandFixture.`활성 브랜드`.toEntity()

            // when
            val actual =cut.aggregateProductDetail(savedProduct, brand)

            // then
            assertThat(actual.likeCount).isEqualTo(LikeCount.ZERO)
        }

        @Test
        fun `삭제된 좋아요 수 정보는 0으로 설정된다`() {
            // given
            val productRepository = TestProductRepository()
            val productLikeCountRepository = TestProductLikeCountRepository()
            val cut = ProductServiceImpl(productRepository, productLikeCountRepository)

            val product = ProductFixture.`활성 상품 1`.toEntity()
            val savedProduct = productRepository.save(product)

            val brand = BrandFixture.`활성 브랜드`.toEntity()

            val productLikeCount = ProductLikeCountFixture.`좋아요 10개`.toEntity().also(ProductLikeCount::delete)
            productLikeCountRepository.save(productLikeCount)

            // when
            val actual =cut.aggregateProductDetail(savedProduct, brand)

            // then
            assertThat(actual.likeCount).isEqualTo(LikeCount.ZERO)
        }
    }
}
