package com.loopers.infrastructure.product.mv

import com.loopers.domain.product.mv.MvProductRankWeekly
import com.loopers.domain.product.mv.MvProductRankWeeklyRepository
import org.springframework.stereotype.Repository

@Repository
class MvProductRankWeeklyRepositoryImpl(
    private val jpaMvProductRankWeeklyRepository: JpaMvProductRankWeeklyRepository
) : MvProductRankWeeklyRepository {
    
    override fun saveAll(weeklyRanks: List<MvProductRankWeekly>) {
        jpaMvProductRankWeeklyRepository.saveAll(weeklyRanks)
    }
}