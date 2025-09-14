package com.loopers.domain.product.event

import java.time.LocalDate

data class ProductLikedEvent(val productId: Long, val userId: Long, val eventDate: LocalDate)
