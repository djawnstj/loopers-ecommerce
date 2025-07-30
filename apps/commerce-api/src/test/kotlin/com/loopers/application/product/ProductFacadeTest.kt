package com.loopers.application.product

import com.loopers.application.product.command.GetProductCommand
import com.loopers.domain.product.fake.TestProductService
import com.loopers.fixture.product.ProductFixture
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class ProductFacadeTest {
    @Test
    fun `상품 목록을 조회할 수 있다`() {
        // given
        val productService = TestProductService()
        val cut = ProductFacade(productService)

        val activeProduct = ProductFixture.`활성 상품 1`.toEntity()
        val inactiveProduct = ProductFixture.`비활성 상품`.toEntity()

        productService.addProducts(listOf(activeProduct, inactiveProduct))

        val command = GetProductCommand(
            brandId = null,
            sortType = null,
            page = 0,
            perPage = 10,
        )

        // when
        val result = cut.getProducts(command)

        // then
        assertThat(result).hasSize(1)
    }
}
