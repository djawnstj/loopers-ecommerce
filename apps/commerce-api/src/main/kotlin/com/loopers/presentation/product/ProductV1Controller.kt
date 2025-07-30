package com.loopers.presentation.product

import com.loopers.application.product.ProductFacade
import com.loopers.presentation.product.dto.GetProductRequest
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class ProductV1Controller(
    private val productFacade: ProductFacade,
) {

    @GetMapping("/api/v1/products")
    fun getProducts(request: GetProductRequest) = productFacade.getProducts(request.toCommand())
}
