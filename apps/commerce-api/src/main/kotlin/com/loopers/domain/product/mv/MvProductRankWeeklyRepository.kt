package com.loopers.domain.product.mv

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import java.time.ZonedDateTime

interface MvProductRankWeeklyRepository {
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