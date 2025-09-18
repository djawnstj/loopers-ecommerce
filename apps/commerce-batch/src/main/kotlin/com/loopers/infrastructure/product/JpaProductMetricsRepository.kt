package com.loopers.infrastructure.product

import com.loopers.domain.product.ProductMetrics
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.time.LocalDate

interface JpaProductMetricsRepository : JpaRepository<ProductMetrics, Long> {
    
    @Query("""
        SELECT pm FROM ProductMetrics pm 
        WHERE pm.metricDate BETWEEN :startDate AND :endDate
        ORDER BY pm.rank ASC
        LIMIT :limit
    """)
    fun findByMetricDateBetweenOrderByRankAscLimit(
        @Param("startDate") startDate: LocalDate,
        @Param("endDate") endDate: LocalDate,
        @Param("limit") limit: Int
    ): List<ProductMetrics>
}