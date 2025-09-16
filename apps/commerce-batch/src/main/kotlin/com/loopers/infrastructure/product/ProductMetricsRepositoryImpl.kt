package com.loopers.infrastructure.product

import com.loopers.domain.product.ProductMetrics
import com.loopers.domain.product.ProductMetricsRepository
import org.springframework.stereotype.Repository
import java.time.LocalDate

@Repository
class ProductMetricsRepositoryImpl(
    private val jpaProductMetricsRepository: JpaProductMetricsRepository
) : ProductMetricsRepository {
    
    override fun findByMetricDateBetweenOrderByRankAscLimit(
        startDate: LocalDate,
        endDate: LocalDate,
        limit: Int
    ): List<ProductMetrics> {
        return jpaProductMetricsRepository.findByMetricDateBetweenOrderByRankAscLimit(startDate, endDate, limit)
    }
}