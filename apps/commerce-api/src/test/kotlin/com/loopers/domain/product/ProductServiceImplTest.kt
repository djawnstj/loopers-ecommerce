package com.loopers.domain.product

import com.loopers.domain.product.params.GetProductParam
import com.loopers.fixture.product.ProductFixture
import com.loopers.infrastructure.product.fake.TestProductRepository
import com.loopers.support.error.CoreException
import com.loopers.support.error.ErrorType
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class ProductServiceImplTest {

    @Nested
    inner class `상품 목록을 조회할 때` {

        @Test
        fun `ACTIVE 상태이고 삭제되지 않은 상품만 조회된다`() {
            // given
            val productRepository = TestProductRepository()
            val cut = ProductServiceImpl(productRepository)

            val activeProduct = ProductFixture.`활성 상품 1`.toEntity()
            val inactiveProduct = ProductFixture.`비활성 상품`.toEntity()
            val deletedProduct = ProductFixture.create(name = "삭제된상품").apply { delete() }

            productRepository.saveAll(listOf(activeProduct, inactiveProduct, deletedProduct))

            val param = GetProductParam(null, null, 0, 10)

            // when
            val result = cut.getProducts(param)

            // then
            assertThat(result).hasSize(1)
                .extracting("name")
                .containsExactly("활성상품 1")
        }

        @Test
        fun `brandId로 필터링하여 조회할 수 있다`() {
            // given
            val productRepository = TestProductRepository()
            val cut = ProductServiceImpl(productRepository)

            val brand1Product1 = ProductFixture.create(name = "브랜드1 상품1", brandId = 1L)
            val brand1Product2 = ProductFixture.create(name = "브랜드1 상품2", brandId = 1L)
            val brand2Product1 = ProductFixture.create(name = "브랜드2 상품1", brandId = 2L)

            productRepository.saveAll(listOf(brand1Product1, brand1Product2, brand2Product1))

            val param = GetProductParam(1L, null, 0, 10)

            // when
            val result = cut.getProducts(param)

            // then
            assertThat(result).hasSize(2)
            assertThat(result.all { it.brandId == 1L }).isTrue()
            assertThat(result.map { it.name }).containsExactlyInAnyOrder("브랜드1 상품1", "브랜드1 상품2")
        }

        @Test
        fun `brandId가 null이면 모든 브랜드의 상품을 조회한다`() {
            // given
            val productRepository = TestProductRepository()
            val cut = ProductServiceImpl(productRepository)

            val brand1Product = ProductFixture.create(name = "브랜드1 상품", brandId = 1L)
            val brand2Product = ProductFixture.create(name = "브랜드2 상품", brandId = 2L)

            productRepository.saveAll(listOf(brand1Product, brand2Product))

            val param = GetProductParam(null, null, 0, 10)

            // when
            val result = cut.getProducts(param)

            // then
            assertThat(result).hasSize(2)
            assertThat(result.map { it.name }).containsExactlyInAnyOrder("브랜드1 상품", "브랜드2 상품")
        }
    }

    @Nested
    inner class `상품 상세 정보를 조회할 때` {

        @Test
        fun `존재하는 상품 ID로 조회하면 해당 상품을 반환 한다`() {
            // given
            val productRepository = TestProductRepository()
            val cut = ProductServiceImpl(productRepository)

            val product = ProductFixture.`활성 상품 1`.toEntity()
            val savedProduct = productRepository.save(product)

            // when
            val result = cut.getActiveProductDetail(savedProduct.id)

            // then
            assertThat(result).isNotNull
        }

        @Test
        fun `존재하지 않는 상품 ID로 조회하면 CoreException PRODUCT_NOT_FOUND 예외를 던진다`() {
            // given
            val productRepository = TestProductRepository()
            val cut = ProductServiceImpl(productRepository)

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
            val productRepository = TestProductRepository()
            val cut = ProductServiceImpl(productRepository)

            val product = ProductFixture.`활성 상품 1`.toEntity().apply { delete() }
            val savedProduct = productRepository.save(product)

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
