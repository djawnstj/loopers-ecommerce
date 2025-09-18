package com.loopers.infrastructure.product.mv

import com.loopers.domain.product.mv.MvProductRankWeekly
import com.loopers.domain.product.mv.MvProductRankWeeklyRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository
import java.time.ZonedDateTime

@Repository
class MvProductRankWeeklyRepositoryImpl(
    private val jpaMvProductRankWeeklyRepository: JpaMvProductRankWeeklyRepository
) : MvProductRankWeeklyRepository {
    
    override fun findByCreatedAtBetween(
        startDateTime: ZonedDateTime,
        endDateTime: ZonedDateTime,
        pageable: Pageable
    ): Page<MvProductRankWeekly> {
        return jpaMvProductRankWeeklyRepository.findByCreatedAtBetween(startDateTime, endDateTime, pageable)
    }
    
    override fun countByCreatedAtBetween(
        startDateTime: ZonedDateTime,
        endDateTime: ZonedDateTime
    ): Long {
        return jpaMvProductRankWeeklyRepository.countByCreatedAtBetween(startDateTime, endDateTime)
    }
}