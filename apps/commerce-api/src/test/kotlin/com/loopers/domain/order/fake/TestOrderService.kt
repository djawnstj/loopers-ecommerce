package com.loopers.domain.order.fake

import com.loopers.domain.order.Order
import com.loopers.domain.order.OrderItem
import com.loopers.domain.order.OrderService
import com.loopers.domain.order.param.SubmitOrderParam
import com.loopers.domain.order.vo.OrderStatusType
import com.loopers.support.error.CoreException
import com.loopers.support.error.ErrorType

class TestOrderService : OrderService {
    private val orders = mutableListOf<Order>()
    private var nextId = 1L

    override fun submitOrder(param: SubmitOrderParam): Order {
        if (param.orderItems.isEmpty()) {
            throw CoreException(ErrorType.REQUIRED_NOT_EMPTY_ORDER_ITEMS, "주문 시 주문 아이템은 1개 이상이어야 합니다.")
        }

        val totalAmount = param.orderItems.sumOf(SubmitOrderParam.OrderItem::productItemPrice)
        val order = Order(param.userId, totalAmount, totalAmount, OrderStatusType.READY)

        // Reflection을 사용하여 ID 설정
        val idField = order.javaClass.superclass.getDeclaredField("id")
        idField.isAccessible = true
        idField.set(order, nextId++)

        val orderItems = param.orderItems
            .filter { it.quantity > 0 }
            .map { item ->
                val orderItem = OrderItem(
                    order,
                    item.productItemId,
                    item.productItemName,
                    item.productItemPrice,
                    item.quantity,
                )

                // OrderItem ID도 설정
                val orderItemIdField = orderItem.javaClass.superclass.getDeclaredField("id")
                orderItemIdField.isAccessible = true
                orderItemIdField.set(orderItem, nextId++)

                orderItem
            }
        order.addItem(orderItems)

        orders.add(order)
        return order
    }

    override fun getOrderByOrderNumber(orderNumber: String): Order =
        orders.find { it.orderNumber == orderNumber } ?: throw CoreException(ErrorType.ORDER_NOT_FOUND)

    override fun getOrderById(id: Long): Order = findById(id) ?: throw CoreException(ErrorType.ORDER_NOT_FOUND)

    override fun pendingOrder(id: Long) {
        val order = findById(id) ?: throw CoreException(ErrorType.ORDER_NOT_FOUND)
        order.pending()
    }

    override fun cancelOrder(id: Long) {
        val order = findById(id) ?: throw CoreException(ErrorType.ORDER_NOT_FOUND)
        order.cancel()
    }

    override fun failedOrder(id: Long) {
        val order = findById(id) ?: throw CoreException(ErrorType.ORDER_NOT_FOUND)
        order.failed()
    }

    override fun getPendingOrders(): List<Order> =
        this.orders.filter { it.status == OrderStatusType.PENDING }

    private fun findById(id: Long): Order? {
        return orders.find { it.id == id }
    }

    fun addOrders(orders: List<Order>) {
        this.orders.addAll(orders)
    }

    fun clear() {
        orders.clear()
        nextId = 1L
    }

    fun findAll(): List<Order> = orders.toList()
}
