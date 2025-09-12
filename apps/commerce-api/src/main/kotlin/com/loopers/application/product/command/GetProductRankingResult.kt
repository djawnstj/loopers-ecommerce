package com.loopers.application.product.command

import com.loopers.domain.product.ProductRankings

data class GetProductRankingResult(
    val rankings: List<RankedProduct>,
    val totalCount: Long,
    val currentPage: Int,
) {
    companion object {
        operator fun invoke(rankedProducts: List<RankedProduct>, rankings: ProductRankings): GetProductRankingResult =
            GetProductRankingResult(
                rankings = rankedProducts,
                totalCount = rankings.totalCount,
                currentPage = rankings.page,
            )
    }

    data class RankedProduct(
        val productId: Long,
        val productName: String,
        val brandId: Long,
        val likeCount: Long,
        val rank: Long,
    )
}
