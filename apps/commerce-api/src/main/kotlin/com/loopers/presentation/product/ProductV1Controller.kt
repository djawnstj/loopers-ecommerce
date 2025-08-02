package com.loopers.presentation.product

import com.loopers.application.product.ProductFacade
import com.loopers.presentation.product.dto.GetProductDetailResponse
import com.loopers.presentation.product.dto.GetProductRequest
import com.loopers.presentation.product.dto.GetProductResponse
import com.loopers.support.presentation.ApiResponse
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

@RestController
class ProductV1Controller(
    private val productFacade: ProductFacade,
) {

    @GetMapping("/api/v1/products")
    fun getProducts(request: GetProductRequest): ApiResponse<GetProductResponse> {
        val result = productFacade.getProducts(request.toCommand())
        return ApiResponse.success(GetProductResponse(result))
    }

    @GetMapping("/api/v1/products/{productId}")
    fun getProductDetail(@PathVariable productId: Long): ApiResponse<GetProductDetailResponse> {
        val result = productFacade.getProductDetail(productId)
        return ApiResponse.success(GetProductDetailResponse(result))
    }
}
