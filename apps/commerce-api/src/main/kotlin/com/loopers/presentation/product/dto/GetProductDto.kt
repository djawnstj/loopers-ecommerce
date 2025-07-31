package com.loopers.presentation.product.dto

import com.loopers.application.product.command.GetProductsCommand
import com.loopers.application.product.command.GetProductsResult
import com.loopers.support.enums.sort.ProductSortType

data class GetProductRequest(
    val brandId: Long?,
    val sortType: ProductSortType?,
    val page: Int,
    val perPage: Int,
) {
    fun toCommand() = GetProductsCommand(brandId, sortType, page, perPage)
}

data class GetProductResponse(
    val products: List<Product>,
) {
    companion object {
        operator fun invoke(result: GetProductsResult): GetProductResponse =
            GetProductResponse(result.products.map(Product::invoke))
    }

    data class Product(
        val id: Long,
        val name: String,
    ) {
        companion object {
            operator fun invoke(product: GetProductsResult.ProductInfo): Product = Product(product.id, product.name)
        }
    }
}
