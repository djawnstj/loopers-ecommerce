package com.loopers.application.product.command

import com.loopers.domain.product.params.GetProductParam
import com.loopers.support.enums.sort.ProductSortType

class GetProductCommand(
    val brandId: Long?,
    val sortType: ProductSortType?,
    val page: Int,
    val perPage: Int,
) {
    fun toParam(): GetProductParam = GetProductParam(brandId, sortType, page, perPage)
}
