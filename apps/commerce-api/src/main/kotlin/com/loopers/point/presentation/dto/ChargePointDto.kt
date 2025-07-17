package com.loopers.point.presentation.dto

import jakarta.validation.constraints.Positive
import java.math.BigDecimal

data class ChargePointRequest(
    @field:Positive(message = "포인트 충전은 0 보다 커야합니다.")
    val amount: BigDecimal,
) {
    init {
        println("값: $amount")
    }
}

data class ChargePointResponse(
    val balance: BigDecimal,
)
