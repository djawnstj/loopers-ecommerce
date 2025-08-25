package com.loopers.domain.payment.vo

import com.loopers.infrastructure.payment.client.TransactionStatusResponse

enum class PaymentStatusType {
    PENDING, FAILED, PAID, REFUNDED, PARTIAL_REFUNDED
    ;

    companion object {
        fun fromPgStatus(status: TransactionStatusResponse): PaymentStatusType =
            when (status) {
                TransactionStatusResponse.PENDING -> PENDING
                TransactionStatusResponse.SUCCESS -> PAID
                TransactionStatusResponse.FAILED -> FAILED
            }
    }
}
