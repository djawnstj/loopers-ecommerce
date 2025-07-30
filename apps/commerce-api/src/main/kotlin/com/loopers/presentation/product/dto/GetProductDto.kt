package com.loopers.presentation.product.dto

import com.loopers.application.product.command.GetProductCommand
import com.loopers.support.enums.sort.ProductSortType

data class GetProductRequest(
    val brandId: Long?,
    val sortType: ProductSortType?,
    val page: Int,
    val perPage: Int,
) {
    fun toCommand() = GetProductCommand(brandId, sortType, page, perPage)
}
