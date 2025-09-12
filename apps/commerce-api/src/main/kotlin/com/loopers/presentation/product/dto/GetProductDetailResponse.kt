package com.loopers.presentation.product.dto

import com.loopers.application.product.command.GetProductDetailResult
import com.loopers.domain.product.vo.ProductStatusType
import java.math.BigDecimal
import java.time.LocalDateTime

data class GetProductDetailResponse(
    val id: Long,
    val name: String,
    val saleStartAt: LocalDateTime,
    val status: ProductStatusType,
    val items: List<Item>,
    val brandId: Long,
    val brandName: String,
    val likeCount: Long,
    val rank: Long?,
) {
    companion object {
        operator fun invoke(result: GetProductDetailResult): GetProductDetailResponse =
            GetProductDetailResponse(
                result.id,
                result.name,
                result.saleStartAt,
                result.status,
                result.items.map(Item::invoke),
                result.brandId,
                result.brandName,
                result.likeCount,
                result.rank,
            )
    }

    data class Item(
        val id: Long,
        val name: String,
        val price: BigDecimal,
    ) {
        companion object {
            operator fun invoke(itemResult: GetProductDetailResult.Item): Item =
                Item(itemResult.id, itemResult.name, itemResult.price)
        }
    }
}
