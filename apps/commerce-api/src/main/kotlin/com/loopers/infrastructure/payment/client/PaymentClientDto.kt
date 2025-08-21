package com.loopers.infrastructure.payment.client

data class PaymentRequest(
    val orderId: String,
    val cardType: String,
    val cardNo: String,
    val amount: Long,
    val callbackUrl: String
)

data class PaymentResponse(
    val transactionKey: String,
    val status: TransactionStatusResponse,
    val reason: String?,
)

enum class TransactionStatusResponse {
    PENDING,
    SUCCESS,
    FAILED,
    ;
}
