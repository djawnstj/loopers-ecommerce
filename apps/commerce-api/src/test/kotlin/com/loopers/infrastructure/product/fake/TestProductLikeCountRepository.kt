package com.loopers.infrastructure.product.fake

import com.loopers.domain.product.ProductLikeCount
import com.loopers.domain.product.ProductLikeCountRepository

class TestProductLikeCountRepository : ProductLikeCountRepository {
    private val likeCountMap = mutableMapOf<Long, ProductLikeCount>()
    private var nextId = 1L

    fun save(productLikeCount: ProductLikeCount): ProductLikeCount {
        val idField = productLikeCount.javaClass.superclass.getDeclaredField("id")
        idField.isAccessible = true
        idField.set(productLikeCount, nextId++)

        likeCountMap[productLikeCount.productId] = productLikeCount
        return productLikeCount
    }

    fun clear() {
        likeCountMap.clear()
        nextId = 1L
    }

    override fun findByProductId(productId: Long): ProductLikeCount? {
        return likeCountMap[productId]?.takeIf { it.deletedAt == null }
    }
}
