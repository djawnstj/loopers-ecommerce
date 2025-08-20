package com.loopers.domain.order

import com.loopers.domain.order.param.SubmitOrderParam
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

interface OrderService {
    fun submitOrder(param: SubmitOrderParam): Order
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
}
