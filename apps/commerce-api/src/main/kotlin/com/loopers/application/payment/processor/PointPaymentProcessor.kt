package com.loopers.application.payment.processor

import com.loopers.application.payment.command.ProcessPayCommand
import com.loopers.application.payment.command.ProcessPointPayCommand
import com.loopers.domain.order.Order
import com.loopers.domain.order.OrderService
import com.loopers.domain.payment.PaymentService
import com.loopers.domain.payment.param.RecordPaidPaymentParam
import com.loopers.domain.payment.vo.PaymentType
import com.loopers.domain.point.UserPointService
import com.loopers.domain.point.vo.Point
import com.loopers.domain.product.ProductService
import com.loopers.domain.product.params.DeductProductItemsQuantityParam
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal

@Component
class PointPaymentProcessor(
    private val userPointService: UserPointService,
    private val paymentService: PaymentService,
    private val orderService: OrderService,
    private val productService: ProductService,
) : PaymentProcessor {
    override fun support(paymentType: PaymentType): Boolean = (paymentType == PaymentType.POINT)

    @Transactional
    override fun process(command: ProcessPayCommand) {
        (command as ProcessPointPayCommand).let {
            val order: Order = orderService.getOrderByOrderNumber(command.orderNumber)
            val userPoint = userPointService.getUserPoint(command.userId)
            val amount = order.payPrice.value

            if (!userPoint.usable(Point(amount))) {
                paymentService.recordFailedPayment(command.toRecordFailedPaymentParam(order.id, amount))
                order.cancel()

                return
            }

            processInternal(
                command.userId,
                order,
                command.toRecordPaidPaymentParam(order.id, amount),
                amount,
            )
        }
    }

    private fun processInternal(
        userId: Long,
        order: Order,
        recordPaidPaymentParam: RecordPaidPaymentParam,
        amount: BigDecimal,
    ) {
        userPointService.useUserPoint(userId, amount)

        paymentService.recordPaidPayment(recordPaidPaymentParam)
        order.complete()
        productService.deductProductItemsQuantity(order.toDeductProductItemsQuantityParam())
    }

    private fun Order.toDeductProductItemsQuantityParam(): DeductProductItemsQuantityParam =
        DeductProductItemsQuantityParam(orderItems.map {
            DeductProductItemsQuantityParam.DeductItem(it.productItemId, it.quantity.value)
        })
}
