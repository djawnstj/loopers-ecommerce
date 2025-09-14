package com.loopers.presentation.product.dto

import com.loopers.application.product.command.GetProductRankingResult

data class GetProductRankingResponse(
    val rankings: List<RankedProduct>,
    val totalCount: Long,
    val currentPage: Int,
) {
    companion object {
        operator fun invoke(result: GetProductRankingResult): GetProductRankingResponse =
            GetProductRankingResponse(
                result.rankings.map(RankedProduct::invoke),
                result.totalCount,
                result.currentPage,
            )
    }

    data class RankedProduct(
        val productId: Long,
        val productName: String,
        val brandId: Long,
        val likeCount: Long,
        val rank: Long,
    ) {
        companion object {
            operator fun invoke(rankedProduct: GetProductRankingResult.RankedProduct): RankedProduct =
                RankedProduct(
                    productId = rankedProduct.productId,
                    productName = rankedProduct.productName,
                    brandId = rankedProduct.brandId,
                    likeCount = rankedProduct.likeCount,
                    rank = rankedProduct.rank,
                )
        }
    }
}
