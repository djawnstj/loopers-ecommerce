package com.loopers.domain.product

import com.loopers.domain.BaseEntity
import com.loopers.domain.product.vo.LikeCount
import jakarta.persistence.Entity
import jakarta.persistence.Table

@Entity
@Table(name = "product_like_count")
class ProductLikeCount private constructor(
    productId: Long,
    count: LikeCount,
) : BaseEntity() {
    var productId: Long = productId
        protected set
    var count: LikeCount = count
        protected set

    companion object {
        operator fun invoke(productId: Long, count: Long): ProductLikeCount =
            ProductLikeCount(productId, LikeCount(count))
    }
}
