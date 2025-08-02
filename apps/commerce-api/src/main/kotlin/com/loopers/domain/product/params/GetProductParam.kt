package com.loopers.domain.product.params

import com.loopers.support.enums.sort.ProductSortType

data class GetProductParam(
    val brandId: Long?,
    val sortType: ProductSortType?,
    val page: Int,
    val perPage: Int,
)
