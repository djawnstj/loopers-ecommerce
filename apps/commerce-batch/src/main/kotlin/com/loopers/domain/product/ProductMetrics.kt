package com.loopers.domain.product

import com.loopers.domain.BaseEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Table
import java.time.LocalDate

@Entity
@Table(name = "product_metrics")
class ProductMetrics(
    productId: Long,
    metricDate: LocalDate,
    likeCount: Int = 0,
    viewCount: Int = 0,
    salesCount: Int = 0,
) : BaseEntity() {
    
    @Column(name = "product_id", nullable = false)
    val productId: Long = productId
    
    @Column(name = "metric_date", nullable = false)
    val metricDate: LocalDate = metricDate
    
    @Column(name = "like_count", nullable = false)
    var likeCount: Int = likeCount
        protected set
    
    @Column(name = "view_count", nullable = false)
    var viewCount: Int = viewCount
        protected set
    
    @Column(name = "sales_count", nullable = false)
    var salesCount: Int = salesCount
        protected set
}