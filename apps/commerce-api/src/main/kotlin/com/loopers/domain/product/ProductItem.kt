package com.loopers.domain.product

import com.loopers.domain.BaseEntity
import com.loopers.domain.product.vo.Price
import com.loopers.domain.product.vo.Quantity
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import java.math.BigDecimal

@Entity
@Table(name = "product_item")
class ProductItem private constructor(
    product: Product,
    price: Price,
    quantity: Quantity,
) : BaseEntity() {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    var product: Product = product
        protected set

    var price: Price = price
        protected set

    var quantity: Quantity = quantity
        protected set

    companion object {
        operator fun invoke(product: Product, price: BigDecimal, quantity: Int): ProductItem =
            ProductItem(product, Price(price), Quantity(quantity))
    }
}
