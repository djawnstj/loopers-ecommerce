package com.loopers.interfaces.consumer.product.dto

import java.time.LocalDate

data class ProductViewedEvent(val productId: Long, val eventDate: LocalDate)
