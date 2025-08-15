package com.loopers.application.product

import com.loopers.application.product.command.GetProductsCommand
import com.loopers.domain.brand.fake.TestBrandService
import com.loopers.domain.product.fake.TestProductService
import com.loopers.domain.product.vo.ProductStatusType
import com.loopers.fixture.brand.BrandFixture
import com.loopers.fixture.product.ProductFixture
import com.loopers.fixture.product.ProductLikeCountFixture
import com.loopers.support.error.CoreException
import com.loopers.support.error.ErrorType
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.time.LocalDateTime

class ProductFacadeTest {
    @Test
    fun `상품 목록을 조회할 수 있다`() {
        // given
        val productService = TestProductService()
        val brandService = TestBrandService()
        val cut = ProductFacade(productService, brandService)

        val activeProduct = ProductFixture.`활성 상품 1`.toEntity()
        val inactiveProduct = ProductFixture.`비활성 상품`.toEntity()

        productService.addProducts(listOf(activeProduct, inactiveProduct))

        val command = GetProductsCommand(
            brandId = null,
            sortType = null,
            page = 0,
            perPage = 10,
        )

        // when
        val actual = cut.getProducts(command)

        // then
        assertThat(actual.products).hasSize(1)
    }

    @Nested
    inner class `상품 상세 정보를 조회할 때` {

        @Test
        fun `존재하지 않는 상품 ID로 조회하면 CoreException PRODUCT_NOT_FOUND 예외를 던진다`() {
            // given
            val productService = TestProductService()
            val brandService = TestBrandService()
            val cut = ProductFacade(productService, brandService)

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
            val productService = TestProductService()
            val brandService = TestBrandService()
            val cut = ProductFacade(productService, brandService)

            val product = ProductFixture.create(name = "테스트상품", brandId = 999L)
            productService.addProducts(listOf(product))

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
            val productService = TestProductService()
            val brandService = TestBrandService()
            val cut = ProductFacade(productService, brandService)

            val product = ProductFixture.`활성 상품 1`.toEntity().apply { delete() }
            productService.addProducts(listOf(product))

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
        fun `좋아요 수 정보가 없으면 0으로 설정된다`() {
            // given
            val productService = TestProductService()
            val brandService = TestBrandService()
            val cut = ProductFacade(productService, brandService)

            val brand = BrandFixture.`활성 브랜드`.toEntity()
            brandService.addBrands(listOf(brand))

            val product = ProductFixture.create(name = "활성상품 1", brandId = brand.id)
            productService.addProducts(listOf(product))

            // when
            val actual = cut.getProductDetail(product.id)

            // then
            assertThat(actual.likeCount).isEqualTo(0L)
        }

        @Test
        fun `존재하는 상품 ID로 조회하면 상품 상세 정보를 반환한다`() {
            // given
            val productService = TestProductService()
            val brandService = TestBrandService()
            val cut = ProductFacade(productService, brandService)

            val brand = BrandFixture.`활성 브랜드`.toEntity()
            brandService.addBrands(listOf(brand))

            val product = ProductFixture.create(
                name = "활성상품 1",
                brandId = brand.id,
                saleStartAt = LocalDateTime.parse("2025-01-01T00:00:00"),
            )
            productService.addProducts(listOf(product))

            val productLikeCount = ProductLikeCountFixture.`좋아요 10개`.toEntity(productId = product.id)
            productService.addProductLikeCount(productLikeCount)

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
                ).containsExactly(
                    "활성상품 1",
                    LocalDateTime.parse("2025-01-01T00:00:00"),
                    ProductStatusType.ACTIVE,
                    "활성브랜드",
                    0L,
                )
        }
    }
}
