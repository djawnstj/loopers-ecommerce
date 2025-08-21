package com.loopers.domain.payment

import com.loopers.domain.BaseEntity
import com.loopers.domain.payment.vo.PaymentStatusType
import com.loopers.domain.payment.vo.PaymentType
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Table
import java.math.BigDecimal

@Entity
@Table(name = "payment")
class Payment(
    orderId: Long,
    paymentKey: String?,
    amount: BigDecimal,
    type: PaymentType,
    status: PaymentStatusType,
) : BaseEntity() {
    var orderId: Long = orderId
        protected set
    var paymentKey: String? = paymentKey
        protected set
    var amount: BigDecimal = amount
        protected set

    @Enumerated(EnumType.STRING)
    var type: PaymentType = type
        protected set

    @Enumerated(EnumType.STRING)
    var status: PaymentStatusType = status
        protected set

    fun paid() {
        this.status = PaymentStatusType.PAID
    }

    fun failed() {
        this.status = PaymentStatusType.FAILED
    }
}
