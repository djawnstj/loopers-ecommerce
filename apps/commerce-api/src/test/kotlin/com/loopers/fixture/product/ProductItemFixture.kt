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
    data object `1 만원 상품` : ProductItemFixture(price = BigDecimal("10000"))
    data object `2 만원 상품` : ProductItemFixture(price = BigDecimal("20000"))

    fun toEntity(product: Product): ProductItem = ProductItem(product, name, price, quantity)
}
