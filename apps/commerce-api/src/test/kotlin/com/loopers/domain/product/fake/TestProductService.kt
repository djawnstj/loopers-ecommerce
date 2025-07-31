package com.loopers.domain.product.fake

import com.loopers.domain.brand.Brand
import com.loopers.domain.product.Product
import com.loopers.domain.product.ProductDetailView
import com.loopers.domain.product.ProductLikeCount
import com.loopers.domain.product.ProductService
import com.loopers.domain.product.params.GetProductParam
import com.loopers.domain.product.vo.LikeCount
import com.loopers.domain.product.vo.ProductStatusType
import com.loopers.support.enums.sort.ProductSortType
import com.loopers.support.error.CoreException
import com.loopers.support.error.ErrorType

class TestProductService : ProductService {
    private val products = mutableListOf<Product>()
    private val likeCountMap = mutableMapOf<Long, ProductLikeCount>()

    fun addProducts(products: List<Product>) {
        this.products.addAll(products)
    }

    fun addProductLikeCount(productLikeCount: ProductLikeCount) {
        likeCountMap[productLikeCount.productId] = productLikeCount
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

    override fun getActiveProductInfo(id: Long): Product {
        return products.find { 
            it.id == id && it.status == ProductStatusType.ACTIVE && it.deletedAt == null 
        } ?: throw CoreException(ErrorType.PRODUCT_NOT_FOUND, "식별자가 $id 에 해당하는 상품 정보를 찾지 못했습니다.")
    }

    override fun aggregateProductDetail(productDetail: Product, brandDetail: Brand): ProductDetailView {
        val productLikeCount = likeCountMap[productDetail.id]
            ?.takeIf { it.deletedAt == null }
            ?.count 
            ?: LikeCount.ZERO

        return ProductDetailView(productDetail, brandDetail, productLikeCount)
    }
}
