package com.loopers.domain.product

import com.loopers.domain.BaseEntity
import com.loopers.domain.product.vo.LikeCount
import jakarta.persistence.Entity
import jakarta.persistence.Table
import jakarta.persistence.Version

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

    @Version
    var version: Long = 0
        protected set

    fun increase() {
        count++
    }

    fun decrease() {
        if (count.isZero()) {
            return
        }
        count--
    }

    companion object {
        operator fun invoke(productId: Long, count: Long): ProductLikeCount =
            ProductLikeCount(productId, LikeCount(count))
    }
}
