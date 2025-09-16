package com.loopers.domain.product.mv

interface MvProductRankWeeklyRepository {
    fun saveAll(weeklyRanks: List<MvProductRankWeekly>)
}