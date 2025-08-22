package com.loopers.domain.order

import com.loopers.domain.BaseEntity
import com.loopers.domain.order.vo.Money
import com.loopers.domain.order.vo.OrderStatusType
import com.loopers.support.error.CoreException
import com.loopers.support.error.ErrorType
import jakarta.persistence.Embedded
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Table
import java.math.BigDecimal
import java.util.UUID

@Entity
@Table(name = "orders")
class Order private constructor(
    userId: Long,
    orderNumber: String,
    totalAmount: Money,
    payPrice: Money,
    status: OrderStatusType,
) : BaseEntity() {
    var userId: Long = userId
        protected set
    var orderNumber: String = orderNumber
        protected set
    var totalAmount: Money = totalAmount
        protected set
    var payPrice: Money = payPrice
        protected set

    @Enumerated(EnumType.STRING)
    var status: OrderStatusType = status
        protected set

    @Embedded
    val orderItems: OrderItems = OrderItems()

    fun addItem(orderItems: List<OrderItem>) {
        if (orderItems.isEmpty()) {
            throw CoreException(ErrorType.REQUIRED_NOT_EMPTY_ORDER_ITEMS)
        }

        this.orderItems.addItems(orderItems)
    }

    fun complete() {
        this.status = OrderStatusType.COMPLETE
    }

    fun cancel() {
        this.status = OrderStatusType.CANCELED
    }

    fun pending() {
        this.status = OrderStatusType.PENDING
    }

    companion object {
        operator fun invoke(userId: Long, totalAmount: BigDecimal, payPrice: BigDecimal, status: OrderStatusType): Order =
            Order(userId, UUID.randomUUID().toString(), Money(totalAmount), Money(payPrice), status)

        fun createNewOrder(userId: Long, totalAmount: BigDecimal, payPrice: BigDecimal): Order =
            invoke(userId, totalAmount, payPrice, OrderStatusType.READY)
    }
}
