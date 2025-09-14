package com.loopers.domain.product.params

import java.time.LocalDate

data class GetProductRankingParam(
    val page: Int,
    val perPage: Int,
    val date: LocalDate,
)