package com.loopers.batch.reader

import com.loopers.domain.product.ProductMetrics
import jakarta.persistence.EntityManagerFactory
import org.springframework.batch.item.database.JpaPagingItemReader
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder
import org.springframework.stereotype.Component
import java.time.LocalDate

@Component
class ProductMetricsItemReader {
    
    fun createWeeklyReader(
        entityManagerFactory: EntityManagerFactory,
        startDate: LocalDate,
        endDate: LocalDate
    ): JpaPagingItemReader<ProductMetrics> {
        return JpaPagingItemReaderBuilder<ProductMetrics>()
            .name("productMetricsWeeklyReader")
            .entityManagerFactory(entityManagerFactory)
            .queryString(
                """
                SELECT pm FROM ProductMetrics pm 
                WHERE pm.metricDate BETWEEN :startDate AND :endDate 
                ORDER BY pm.productId, pm.metricDate
                """.trimIndent()
            ).parameterValues(
                mapOf(
                    "startDate" to startDate,
                    "endDate" to endDate
                )
            ).pageSize(1000)
            .build()
    }
    
    fun createMonthlyReader(
        entityManagerFactory: EntityManagerFactory,
        startDate: LocalDate,
        endDate: LocalDate
    ): JpaPagingItemReader<ProductMetrics> {
        return JpaPagingItemReaderBuilder<ProductMetrics>()
            .name("productMetricsMonthlyReader")
            .entityManagerFactory(entityManagerFactory)
            .queryString(
                """
                SELECT pm FROM ProductMetrics pm 
                WHERE pm.metricDate BETWEEN :startDate AND :endDate 
                ORDER BY pm.productId, pm.metricDate
                """.trimIndent()
            ).parameterValues(
                mapOf(
                    "startDate" to startDate,
                    "endDate" to endDate
                )
            ).pageSize(1000)
            .build()
    }

    // TODO 월 단위 집계는 주 단위 집계 테이블을 통해? 아님 메트릭 테이블을 통해?
}
