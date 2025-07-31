package com.loopers.fixture.product

import com.loopers.domain.product.Product
import com.loopers.domain.product.ProductItem
import java.math.BigDecimal

sealed class ProductItemFixture(
    val name: String = "검은색 라지",
    val price: BigDecimal = BigDecimal("10000"),
    val quantity: Int = 10,
) {
    data object 기본 : ProductItemFixture()
    data object `검은색 라지 만원` : ProductItemFixture()
    data object `빨간색 라지 만원` : ProductItemFixture(name = "빨간색 라지")

    fun toEntity(product: Product): ProductItem = ProductItem(product, name, price, quantity)
}
