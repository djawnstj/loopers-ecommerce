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
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal

@Component
class PointPaymentProcessor(
    private val userPointService: UserPointService,
    private val paymentService: PaymentService,
    private val orderService: OrderService,
) : PaymentProcessor {
    override fun support(paymentType: PaymentType): Boolean = (paymentType == PaymentType.POINT)

    @Transactional
    override fun process(command: ProcessPayCommand) {
        (command as ProcessPointPayCommand).let {
            val order: Order = orderService.getOrderById(command.orderId)
            val userPoint = userPointService.getUserPoint(command.userId)
            val amount = order.payPrice.value

            if (!userPoint.usable(Point(amount))) {
                paymentService.recordFailedPayment(command.toRecordFailedPaymentParam(amount))
                orderService.cancelOrder(order.id)

                return
            }

            processInternal(
                command.userId,
                command.orderId,
                command.toRecordPaidPaymentParam(amount),
                amount,
            )
        }
    }

    private fun processInternal(
        userId: Long,
        orderId: Long,
        recordPaidPaymentParam: RecordPaidPaymentParam,
        amount: BigDecimal,
    ) {
        userPointService.useUserPoint(userId, amount)

        paymentService.recordPaidPayment(recordPaidPaymentParam)
        orderService.completeOrder(orderId)
    }
}
