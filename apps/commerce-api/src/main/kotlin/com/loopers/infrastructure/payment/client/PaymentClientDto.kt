package com.loopers.infrastructure.payment.client

import com.loopers.support.enums.payment.CardType

data class PaymentRequest(
    val orderId: String,
    val cardType: String,
    val cardNo: String,
    val amount: Long,
    val callbackUrl: String,
)

data class PaymentResponse(
    val transactionKey: String,
    val status: TransactionStatusResponse,
    val reason: String?,
)

data class TransactionDetailResponse(
    val transactionKey: String,
    val orderId: String,
    val cardType: CardType,
    val cardNo: String,
    val amount: Long,
    val status: TransactionStatusResponse,
    val reason: String?,
)

data class OrderResponse(
    val orderId: String,
    val transactions: List<TransactionResponse>,
)

data class TransactionResponse(
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
