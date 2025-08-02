package com.loopers.domain.product

import com.loopers.domain.BaseEntity
import com.loopers.domain.product.vo.Price
import com.loopers.domain.product.vo.Quantity
import com.loopers.support.error.CoreException
import com.loopers.support.error.ErrorType
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
    name: String,
    price: Price,
    quantity: Quantity,
) : BaseEntity() {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    var product: Product = product
        protected set

    var name: String = name
        protected set

    var price: Price = price
        protected set

    var quantity: Quantity = quantity
        protected set

    fun deduct(deductQuantity: Int) {
        if (deductQuantity < 0 || quantity.value < deductQuantity) {
            throw CoreException(ErrorType.INSUFFICIENT_PRODUCT_QUANTITY)
        }

        val newQuantity = quantity.value - deductQuantity
        quantity = Quantity(newQuantity)
    }

    companion object {
        operator fun invoke(product: Product, name: String, price: BigDecimal, quantity: Int): ProductItem =
            ProductItem(product, name, Price(price), Quantity(quantity))
    }
}
