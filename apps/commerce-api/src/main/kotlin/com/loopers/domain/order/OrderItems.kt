package com.loopers.domain.order

import jakarta.persistence.Embeddable
import jakarta.persistence.OneToMany

@Embeddable
class OrderItems(
    @OneToMany(mappedBy = "order")
    val items: MutableList<OrderItem> = mutableListOf(),
) : Iterable<OrderItem> by items {
    fun addItems(orderItem: List<OrderItem>) {
        items.addAll(orderItem)
    }

    fun isEmpty(): Boolean = items.isEmpty()
}
