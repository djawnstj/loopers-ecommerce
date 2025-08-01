package com.loopers.infrastructure.product.fake

import com.loopers.domain.product.ProductItem
import com.loopers.domain.product.ProductItemRepository

class TestProductItemRepository : ProductItemRepository {
    private val productItems = mutableListOf<ProductItem>()
    private var nextId = 1L

    fun saveAll(productItems: List<ProductItem>): List<ProductItem> {
        return productItems.map { save(it) }
    }

    fun save(productItem: ProductItem): ProductItem {
        val idField = productItem.javaClass.superclass.getDeclaredField("id")
        idField.isAccessible = true
        idField.set(productItem, nextId++)

        this.productItems.add(productItem)
        return productItem
    }

    override fun findAllByIds(productItemIds: List<Long>): List<ProductItem> {
        if (productItemIds.isEmpty()) {
            return emptyList()
        }

        return productItems.filter { productItem ->
            productItem.id in productItemIds && productItem.deletedAt == null
        }
    }

    fun clear() {
        productItems.clear()
        nextId = 1L
    }
}
