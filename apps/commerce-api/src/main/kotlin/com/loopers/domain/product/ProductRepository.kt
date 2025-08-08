package com.loopers.domain.product

import com.loopers.support.enums.sort.ProductSortType

interface ProductRepository {
    fun findBySortType(brandId: Long?, sortBy: ProductSortType?, offset: Int, limit: Int): List<Product>
    fun findActiveProductById(id: Long): Product?
    fun findProductItemsByIds(productItemIds: List<Long>): List<ProductItem>
    fun findProductItemByProductItemIdWithPessimisticWrite(productItemId: Long): ProductItem?
}
