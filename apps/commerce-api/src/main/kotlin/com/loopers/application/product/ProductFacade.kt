package com.loopers.application.product

import com.loopers.application.product.command.GetProductCommand
import com.loopers.domain.brand.BrandService
import com.loopers.domain.product.ProductService
import org.springframework.stereotype.Component

@Component
class ProductFacade(
    private val productService: ProductService,
    private val brandService: BrandService,
) {
    fun getProducts(command: GetProductCommand) = productService.getProducts(command.toParam())

    fun getProductAndBrand(productId: Long) {
        val productDetail = productService.getActiveProductDetail(productId)
        val brandDetail = brandService.getActiveBrandDetail(productDetail.brandId)

        productService.aggregateProductDetail(productDetail, brandDetail)
    }
}
