package com.loopers.application.product

import com.loopers.application.product.command.GetProductDetailResult
import com.loopers.application.product.command.GetProductsCommand
import com.loopers.application.product.command.GetProductsResult
import com.loopers.domain.brand.BrandService
import com.loopers.domain.product.ProductService
import org.springframework.stereotype.Component

@Component
class ProductFacade(
    private val productService: ProductService,
    private val brandService: BrandService,
) {
    fun getProducts(command: GetProductsCommand): GetProductsResult =
        GetProductsResult(productService.getProducts(command.toParam()))

    fun getProductDetail(productId: Long): GetProductDetailResult {
        val productInfo = productService.getActiveProductInfo(productId)
        val brandInfo = brandService.getActiveBrandDetail(productInfo.brandId)

        return GetProductDetailResult(productService.aggregateProductDetail(productInfo, brandInfo))
    }
}
