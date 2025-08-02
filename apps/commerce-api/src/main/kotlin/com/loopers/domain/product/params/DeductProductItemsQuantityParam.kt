package com.loopers.domain.product.params

data class DeductProductItemsQuantityParam(
    val items: List<DeductItem>,
) {
    data class DeductItem(
        val productItemId: Long,
        val quantity: Int,
    )
}
