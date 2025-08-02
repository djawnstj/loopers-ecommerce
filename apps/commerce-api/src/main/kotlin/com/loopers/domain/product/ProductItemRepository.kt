package com.loopers.domain.product

interface ProductItemRepository {
    fun findAllByIds(productItemIds: List<Long>): List<ProductItem>
}
