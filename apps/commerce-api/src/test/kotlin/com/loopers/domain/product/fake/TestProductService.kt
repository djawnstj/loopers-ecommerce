package com.loopers.domain.product.fake

import com.loopers.domain.product.Product
import com.loopers.domain.product.ProductService
import com.loopers.domain.product.params.GetProductParam
import com.loopers.domain.product.vo.ProductStatusType
import com.loopers.support.enums.sort.ProductSortType
import com.loopers.support.error.CoreException
import com.loopers.support.error.ErrorType

class TestProductService : ProductService {
    private val products = mutableListOf<Product>()

    fun addProducts(products: List<Product>) {
        this.products.addAll(products)
    }

    override fun getProducts(param: GetProductParam): List<Product> {
        var activeProducts = products.filter { 
            it.status == ProductStatusType.ACTIVE && it.deletedAt == null 
        }
        
        if (param.brandId != null) {
            activeProducts = activeProducts.filter { it.brandId == param.brandId }
        }

        val sorted = when (param.sortType) {
            ProductSortType.LATEST -> activeProducts.sortedByDescending { it.saleStartAt }
            else -> activeProducts.sortedByDescending { products.indexOf(it) }
        }

        return sorted.drop(param.page * param.perPage).take(param.perPage)
    }

    override fun getActiveProductDetail(id: Long): Product {
        return products.find { 
            it.id == id && it.status == ProductStatusType.ACTIVE && it.deletedAt == null 
        } ?: throw CoreException(ErrorType.PRODUCT_NOT_FOUND, "상품 식별자가 $id 에 해당하는 상품 정보를 찾지 못했습니다.")
    }
}
