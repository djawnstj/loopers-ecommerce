package com.loopers.interfaces.consumer.product.dto

import java.time.LocalDate

data class ProductLikedEvent(val productId: Long, val userId: Long, val eventDate: LocalDate)
