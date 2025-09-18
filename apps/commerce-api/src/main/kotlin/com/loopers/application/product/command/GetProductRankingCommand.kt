package com.loopers.application.product.command

import com.loopers.domain.product.RankingPeriod
import org.springframework.data.domain.Pageable
import java.time.LocalDate

data class GetProductRankingCommand(
    val pageable: Pageable,
    val date: LocalDate = LocalDate.now(),
    val period: RankingPeriod = RankingPeriod.DAILY,
)