package com.loopers.domain.order

import com.loopers.domain.BaseEntity
import com.loopers.domain.order.vo.Money
import com.loopers.domain.product.vo.Quantity
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import java.math.BigDecimal

@Entity
@Table(name = "order_item")
class OrderItem private constructor(
    order: Order,
    productItemId: Long,
    productName: String,
    productPrice: Money,
    quantity: Quantity,
) : BaseEntity() {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    var order: Order = order
        protected set
    var productItemId: Long = productItemId
        protected set
    var productName: String = productName
        protected set
    var productPrice: Money = productPrice
        protected set
    var quantity: Quantity = quantity
        protected set

    companion object {
        operator fun invoke(
            order: Order,
            productItemId: Long,
            productName: String,
            productPrice: BigDecimal,
            quantity: Int,
        ): OrderItem =
            OrderItem(
                order,
                productItemId,
                productName,
                Money(productPrice),
                Quantity(quantity),
            )
    }
}
