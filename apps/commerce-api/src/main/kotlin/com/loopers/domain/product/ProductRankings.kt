package com.loopers.domain.product

data class ProductRankings(
    val rankings: List<ProductRanking>,
    val totalCount: Long,
    val page: Int,
    val perPage: Int,
) {
    val totalPages: Int = if (totalCount == 0L) 1 else ((totalCount - 1) / perPage + 1).toInt()
    val hasNext: Boolean = page < totalPages - 1
    val hasPrevious: Boolean = page > 0
    val isFirst: Boolean = page == 0
    val isLast: Boolean = page == totalPages - 1

    data class ProductRanking(
        val productId: Long,
        val rank: Long,
    )
}
