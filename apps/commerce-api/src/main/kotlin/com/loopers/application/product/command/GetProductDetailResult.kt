package com.loopers.application.product.command

import com.loopers.domain.product.ProductDetailView
import com.loopers.domain.product.ProductItem
import com.loopers.domain.product.vo.ProductStatusType
import java.math.BigDecimal
import java.time.LocalDateTime

data class GetProductDetailResult(
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
        operator fun invoke(productDetailView: ProductDetailView, rank: Long? = null): GetProductDetailResult =
            GetProductDetailResult(
                productDetailView.id,
                productDetailView.name,
                productDetailView.saleStartAt,
                productDetailView.status,
                productDetailView.items.map(Item::invoke),
                productDetailView.brandId,
                productDetailView.brandName,
                productDetailView.likeCount.value,
                rank,
            )
    }

    data class Item(
        val id: Long,
        val name: String,
        val price: BigDecimal,
    ) {
        companion object {
            operator fun invoke(productItem: ProductItem): Item = Item(
                productItem.id,
                productItem.name,
                productItem.price.value,
            )
        }
    }
}
