package com.loopers.batch.dto

import java.time.LocalDate

data class ProductRankingAggregation(
    val productId: Long,
    val productName: String,
    val brandId: Long,
    val likeCount: Long,
    val viewCount: Long,
    val salesCount: Long,
    val score: Double,
    val periodStartDate: LocalDate,
    val periodEndDate: LocalDate,
) {
    fun calculateScore(): Double {
        return (viewCount * VIEW_WEIGHT) + (likeCount * LIKE_WEIGHT) + (salesCount * SALES_WEIGHT)
    }
    
    companion object {
        private const val VIEW_WEIGHT = 0.1
        private const val LIKE_WEIGHT = 0.2
        private const val SALES_WEIGHT = 0.7
    }
}