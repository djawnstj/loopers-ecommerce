package com.loopers.domain.product.mv

interface MvProductRankMonthlyRepository {
    fun saveAll(monthlyRanks: List<MvProductRankMonthly>)
}