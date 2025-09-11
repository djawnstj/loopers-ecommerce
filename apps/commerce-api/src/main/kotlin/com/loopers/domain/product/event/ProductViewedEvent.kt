package com.loopers.domain.product.event

import java.time.LocalDate

data class ProductViewedEvent(val productId: Long, val eventDate: LocalDate)
