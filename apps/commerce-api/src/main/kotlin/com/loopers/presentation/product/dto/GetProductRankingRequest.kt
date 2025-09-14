package com.loopers.presentation.product.dto

import com.loopers.application.product.command.GetProductRankingCommand
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.format.annotation.DateTimeFormat
import java.time.LocalDate

data class GetProductRankingRequest(
    val page: Int = 0,
    val size: Int = 20,
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    val date: LocalDate = LocalDate.now(),
) {
    fun toCommand(): GetProductRankingCommand = GetProductRankingCommand(
        pageable = PageRequest.of(page, size),
        date = date
    )
}