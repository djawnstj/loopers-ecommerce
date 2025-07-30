package com.loopers.infrastructure.product.fake

import com.loopers.domain.product.Product
import com.loopers.domain.product.ProductRepository
import com.loopers.domain.product.vo.ProductStatusType
import com.loopers.support.enums.sort.ProductSortType

class TestProductRepository : ProductRepository {
    private val products = mutableListOf<Product>()
    private var nextId = 1L

    fun saveAll(products: List<Product>): List<Product> {
        return products.map { save(it) }
    }

    fun save(product: Product): Product {
        val idField = product.javaClass.superclass.getDeclaredField("id")
        idField.isAccessible = true
        idField.set(product, nextId++)

        this.products.add(product)
        return product
    }

    override fun findBySortType(brandId: Long?, sortBy: ProductSortType?, offset: Int, limit: Int): List<Product> {
        var filtered = products.filter { 
            it.status == ProductStatusType.ACTIVE && it.deletedAt == null 
        }
        
        if (brandId != null) {
            filtered = filtered.filter { it.brandId == brandId }
        }

        val sorted = when (sortBy) {
            ProductSortType.LATEST -> filtered.sortedByDescending { it.saleStartAt }
            else -> filtered.sortedByDescending { it.id }
        }

        return sorted.drop(offset).take(limit)
    }

    override fun findActiveProductById(id: Long): Product? {
        return products.find { 
            it.id == id && it.status == ProductStatusType.ACTIVE && it.deletedAt == null 
        }
    }
}
