package com.loopers.infrastructure.product.mv

import com.loopers.domain.product.mv.MvProductRankMonthly
import com.loopers.domain.product.mv.MvProductRankMonthlyRepository
import org.springframework.stereotype.Repository

@Repository
class MvProductRankMonthlyRepositoryImpl(
    private val jpaMvProductRankMonthlyRepository: JpaMvProductRankMonthlyRepository
) : MvProductRankMonthlyRepository {
    
    override fun saveAll(monthlyRanks: List<MvProductRankMonthly>) {
        jpaMvProductRankMonthlyRepository.saveAll(monthlyRanks)
    }
}