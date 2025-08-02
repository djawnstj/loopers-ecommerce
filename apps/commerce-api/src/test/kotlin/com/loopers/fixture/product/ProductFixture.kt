package com.loopers.fixture.product

import com.loopers.domain.product.Product
import com.loopers.domain.product.vo.ProductStatusType
import java.time.LocalDateTime

sealed class ProductFixture(
    val brandId: Long = 1L,
    val name: String = "상품",
    val saleStartAt: LocalDateTime = LocalDateTime.parse("2025-01-01T00:00:00"),
    val status: ProductStatusType = ProductStatusType.ACTIVE,
) {
    data object 기본 : ProductFixture()
    data object `25년 1월 1일 판매 상품` : ProductFixture(name = "상품", saleStartAt = LocalDateTime.parse("2025-01-01T00:00:00"))
    data object `25년 1월 2일 판매 상품` : ProductFixture(name = "상품", saleStartAt = LocalDateTime.parse("2025-01-02T00:00:00"))

    data object `활성 상품 1` : ProductFixture(name = "활성상품 1", status = ProductStatusType.ACTIVE)
    data object `활성 상품 2` : ProductFixture(name = "활성상품 2", status = ProductStatusType.ACTIVE)
    data object `비활성 상품` : ProductFixture(name = "비활성상품", status = ProductStatusType.INACTIVE)

    fun toEntity(brandId: Long = this.brandId): Product = Product(brandId, name, saleStartAt, status)

    companion object {
        fun create(
            brandId: Long = 1L,
            name: String = "상품",
            saleStartAt: LocalDateTime = LocalDateTime.now(),
            status: ProductStatusType = ProductStatusType.ACTIVE,
        ): Product = Product(brandId, name, saleStartAt, status)
    }
}
