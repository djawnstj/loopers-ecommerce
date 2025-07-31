package com.loopers.application.product.command

import com.loopers.domain.product.Product
import com.loopers.domain.product.params.GetProductParam
import com.loopers.support.enums.sort.ProductSortType

data class GetProductsCommand(
    val brandId: Long?,
    val sortType: ProductSortType?,
    val page: Int,
    val perPage: Int,
) {
    fun toParam(): GetProductParam = GetProductParam(brandId, sortType, page, perPage)
}

data class GetProductsResult(
    val products: List<ProductInfo>,
) {
    companion object {
        operator fun invoke(products: List<Product>): GetProductsResult =
            GetProductsResult(products.map(ProductInfo::invoke))
    }

    data class ProductInfo(
        val id: Long,
        val name: String,
    ) {
        companion object {
            operator fun invoke(product: Product): ProductInfo =
                ProductInfo(product.id, product.name)
        }
    }
}
