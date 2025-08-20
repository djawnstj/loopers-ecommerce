package com.loopers.domain.order

import com.loopers.domain.order.param.SubmitOrderParam
import com.loopers.support.error.CoreException
import com.loopers.support.error.ErrorType
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

interface OrderService {
    fun submitOrder(param: SubmitOrderParam): Order
    fun getOrderById(id: Long): Order
    fun completeOrder(id: Long)
    fun cancelOrder(id: Long)
}

@Service
@Transactional(readOnly = true)
class OrderServiceImpl(
    private val orderRepository: OrderRepository,
) : OrderService {

    @Transactional
    override fun submitOrder(param: SubmitOrderParam): Order {
        val order = Order.createNewOrder(param.userId, param.totalAmount, param.payPrice)

        val orderItems = param.orderItems
            .filter { it.quantity > 0 }
            .map { item ->
                OrderItem(
                    order,
                    item.productItemId,
                    item.productItemName,
                    item.productItemPrice,
                    item.quantity,
                )
            }
        order.addItem(orderItems)

        return orderRepository.save(order)
    }

    override fun getOrderById(id: Long): Order =
        orderRepository.findById(id) ?: throw CoreException(ErrorType.ORDER_NOT_FOUND)

    @Transactional
    override fun completeOrder(id: Long) {
        orderRepository.findById(id)?.complete()
            ?: throw CoreException(ErrorType.ORDER_NOT_FOUND)
    }

    @Transactional
    override fun cancelOrder(id: Long) {
        orderRepository.findById(id)?.cancel()
            ?: throw CoreException(ErrorType.ORDER_NOT_FOUND)
    }
}
