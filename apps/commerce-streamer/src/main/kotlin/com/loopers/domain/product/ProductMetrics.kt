package com.loopers.domain.product

import com.loopers.domain.BaseEntity
import jakarta.persistence.Entity
import jakarta.persistence.Table
import java.time.LocalDate

@Entity
@Table(name = "product_metric")
class ProductMetric(
    productId: Long,
    metricDate: LocalDate,
    likeCount: Int = 0,
    viewCount: Int = 0,
    salesCount: Int = 0,
) : BaseEntity() {
    val productId: Long = productId
    val metricDate: LocalDate = metricDate
    var likeCount: Int = likeCount
        protected set
    var viewCount: Int = viewCount
        protected set
    var salesCount: Int = salesCount
        protected set
}
