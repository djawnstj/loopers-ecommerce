package com.loopers.interfaces.consumer.order.dto

import java.time.LocalDate

data class OrderCompletedEvent(val orderId: Long, val eventDate: LocalDate)
