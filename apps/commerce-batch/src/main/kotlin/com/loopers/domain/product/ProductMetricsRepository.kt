package com.loopers.domain.product

import java.time.LocalDate

interface ProductMetricsRepository {
    fun findByMetricDateBetweenOrderByRankAscLimit(
        startDate: LocalDate,
        endDate: LocalDate,
        limit: Int
    ): List<ProductMetrics>
}