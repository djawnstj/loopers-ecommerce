package com.loopers.domain.product

interface ProductLikeCountRepository {
    fun save(productLikeCount: ProductLikeCount): ProductLikeCount
    fun findByProductId(productId: Long): ProductLikeCount?
}
