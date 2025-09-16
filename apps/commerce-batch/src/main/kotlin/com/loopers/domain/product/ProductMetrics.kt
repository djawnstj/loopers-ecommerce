package com.loopers.domain.product

import com.loopers.domain.BaseEntity
import jakarta.persistence.Entity
import jakarta.persistence.Table
import java.time.LocalDate

@Entity
@Table(name = "product_metrics")
class ProductMetrics(
    val productId: Long,
    val productName: String,
    val brandId: Long,
    val likeCount: Long,
    val rank: Long,
    val metricDate: LocalDate,
) : BaseEntity()
