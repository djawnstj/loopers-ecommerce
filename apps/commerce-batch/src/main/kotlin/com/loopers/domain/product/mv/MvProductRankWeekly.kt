package com.loopers.domain.product.mv

import com.loopers.domain.BaseEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Table
import java.time.LocalDate

@Entity
@Table(name = "mv_product_rank_weekly")
class MvProductRankWeekly(
    productId: Long,
    productName: String,
    brandId: Long,
    likeCount: Long,
    score: Double,
    rank: Long,
    weekStartDate: LocalDate,
    weekEndDate: LocalDate,
) : BaseEntity() {
    var productId: Long = productId
        protected set
    var productName: String = productName
        protected set
    var brandId: Long = brandId
        protected set
    var likeCount: Long = likeCount
        protected set
    var score: Double = score
        protected set
    var rank: Long = rank
        protected set
    var weekStartDate: LocalDate = weekStartDate
        protected set
    var weekEndDate: LocalDate = weekEndDate
        protected set
}
