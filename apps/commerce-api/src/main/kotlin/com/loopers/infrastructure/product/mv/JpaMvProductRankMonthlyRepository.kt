package com.loopers.infrastructure.product.mv

import com.loopers.domain.product.mv.MvProductRankMonthly
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import java.time.ZonedDateTime

interface JpaMvProductRankMonthlyRepository : JpaRepository<MvProductRankMonthly, Long> {
    fun findByCreatedAtBetween(
        startDateTime: ZonedDateTime,
        endDateTime: ZonedDateTime,
        pageable: Pageable
    ): Page<MvProductRankMonthly>
    
    fun countByCreatedAtBetween(
        startDateTime: ZonedDateTime,
        endDateTime: ZonedDateTime
    ): Long
}