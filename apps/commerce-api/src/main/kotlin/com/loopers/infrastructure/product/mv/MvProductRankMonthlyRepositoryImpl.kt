package com.loopers.infrastructure.product.mv

import com.loopers.domain.product.mv.MvProductRankMonthly
import com.loopers.domain.product.mv.MvProductRankMonthlyRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository
import java.time.ZonedDateTime

@Repository
class MvProductRankMonthlyRepositoryImpl(
    private val jpaMvProductRankMonthlyRepository: JpaMvProductRankMonthlyRepository
) : MvProductRankMonthlyRepository {
    
    override fun findByCreatedAtBetween(
        startDateTime: ZonedDateTime,
        endDateTime: ZonedDateTime,
        pageable: Pageable
    ): Page<MvProductRankMonthly> {
        return jpaMvProductRankMonthlyRepository.findByCreatedAtBetween(startDateTime, endDateTime, pageable)
    }
    
    override fun countByCreatedAtBetween(
        startDateTime: ZonedDateTime,
        endDateTime: ZonedDateTime
    ): Long {
        return jpaMvProductRankMonthlyRepository.countByCreatedAtBetween(startDateTime, endDateTime)
    }
}