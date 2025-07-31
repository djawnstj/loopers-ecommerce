package com.loopers.fixture.product

import com.loopers.domain.product.ProductLikeCount

sealed class ProductLikeCountFixture(
    val productId: Long = 1L,
    val count: Long = 0L,
) {
    data object 기본 : ProductLikeCountFixture()
    data object `좋아요 10개` : ProductLikeCountFixture(productId = 1L, count = 10L)

    fun toEntity(productId: Long = this.productId): ProductLikeCount = ProductLikeCount(productId, count)
}
