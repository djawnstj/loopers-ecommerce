package com.loopers.domain.product

import jakarta.persistence.Embeddable
import jakarta.persistence.OneToMany
import java.math.BigDecimal

@Embeddable
class ProductItems(
    @OneToMany(mappedBy = "product")
    val items: MutableList<ProductItem> = mutableListOf(),
) : Iterable<ProductItem> by items {
    val totalAmount: BigDecimal = items.sumOf { it.price.value }

    fun addItem(item: ProductItem) {
        items.add(item)
    }
}
