package com.loopers.domain.order.event

import java.time.LocalDate

data class OrderCompletedEvent(val orderId: Long, val eventDate: LocalDate)
