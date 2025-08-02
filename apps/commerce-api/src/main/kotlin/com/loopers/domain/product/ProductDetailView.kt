package com.loopers.domain.product

import com.loopers.domain.brand.Brand
import com.loopers.domain.product.vo.LikeCount
import com.loopers.domain.product.vo.ProductStatusType
import java.time.LocalDateTime

data class ProductDetailView(
    val id: Long,
    val name: String,
    val saleStartAt: LocalDateTime,
    val status: ProductStatusType,
    val items: ProductItems,
    val brandId: Long,
    val brandName: String,
    val likeCount: LikeCount,
) {
    companion object {
        operator fun invoke(product: Product, brand: Brand, likeCount: LikeCount): ProductDetailView =
            ProductDetailView(
                product.id,
                product.name,
                product.saleStartAt,
                product.status,
                product.items,
                brand.id,
                brand.name,
                likeCount,
            )
    }
}
