package com.loopers.infrastructure.product.mv

import com.loopers.domain.product.mv.MvProductRankWeekly
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import java.time.ZonedDateTime

interface JpaMvProductRankWeeklyRepository : JpaRepository<MvProductRankWeekly, Long> {
    fun findByCreatedAtBetween(
        startDateTime: ZonedDateTime,
        endDateTime: ZonedDateTime,
        pageable: Pageable
    ): Page<MvProductRankWeekly>
    
    fun countByCreatedAtBetween(
        startDateTime: ZonedDateTime,
        endDateTime: ZonedDateTime
    ): Long
}