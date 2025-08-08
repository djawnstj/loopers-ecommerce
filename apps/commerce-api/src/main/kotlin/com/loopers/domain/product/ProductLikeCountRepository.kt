package com.loopers.domain.product

interface ProductLikeCountRepository {
    fun save(productLikeCount: ProductLikeCount): ProductLikeCount
    fun findByProductIdWithOptimisticLock(productId: Long): ProductLikeCount?
}
