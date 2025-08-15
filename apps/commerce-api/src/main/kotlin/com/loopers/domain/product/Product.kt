package com.loopers.domain.product

import com.loopers.domain.BaseEntity
import com.loopers.domain.product.vo.LikeCount
import com.loopers.domain.product.vo.ProductStatusType
import jakarta.persistence.Embedded
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Table
import jakarta.persistence.Version
import java.time.LocalDateTime

@Entity
@Table(name = "product")
class Product(
    brandId: Long,
    name: String,
    saleStartAt: LocalDateTime,
    status: ProductStatusType = ProductStatusType.ACTIVE,
) : BaseEntity() {
    var brandId: Long = brandId
        protected set

    var name: String = name
        protected set

    var saleStartAt: LocalDateTime = saleStartAt
        protected set

    @Embedded
    var items: ProductItems = ProductItems()
        protected set

    @Enumerated(EnumType.STRING)
    var status: ProductStatusType = status
        protected set
    var likeCount: LikeCount = LikeCount.ZERO
        protected set

    @Version
    var version: Long = 0
        protected set

    fun increaseLikeCount() {
        likeCount++
    }

    fun decreaseLikeCount() {
        if (likeCount.isZero()) {
            return
        }
        likeCount--
    }

}
