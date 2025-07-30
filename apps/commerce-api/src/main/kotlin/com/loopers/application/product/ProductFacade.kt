package com.loopers.application.product

import com.loopers.application.product.command.GetProductCommand
import com.loopers.domain.product.ProductService
import org.springframework.stereotype.Component

@Component
class ProductFacade(
    private val productService: ProductService,
) {
    fun getProducts(command: GetProductCommand) = productService.getProducts(command.toParam())
}
