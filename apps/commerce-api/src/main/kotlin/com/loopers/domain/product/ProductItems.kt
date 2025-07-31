package com.loopers.domain.product

import jakarta.persistence.Embeddable
import jakarta.persistence.OneToMany

@Embeddable
class ProductItems(
    @OneToMany(mappedBy = "product")
    val items: MutableList<ProductItem> = mutableListOf(),
) : Iterable<ProductItem> by items {
    fun addItem(item: ProductItem) {
        items.add(item)
    }
}
