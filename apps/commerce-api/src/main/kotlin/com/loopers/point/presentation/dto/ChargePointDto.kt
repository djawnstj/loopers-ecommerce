package com.loopers.point.presentation.dto

import java.math.BigDecimal

data class ChargePointRequest(
    val amount: BigDecimal,
)

data class ChargePointResponse(
    val balance: BigDecimal,
)
