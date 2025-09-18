package com.loopers.domain.product.mv

import com.loopers.domain.BaseEntity
import jakarta.persistence.Entity
import jakarta.persistence.Table

@Entity
@Table(name = "mv_product_rank_weekly")
class MvProductRankWeekly(
    val productId: Long,
    val productName: String,
    val brandId: Long,
    val likeCount: Long,
    val rank: Long,
) : BaseEntity()