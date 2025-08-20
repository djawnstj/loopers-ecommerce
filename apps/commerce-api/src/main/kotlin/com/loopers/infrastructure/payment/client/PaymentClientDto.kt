package com.loopers.infrastructure.payment.client

data class PaymentRequest(
    val orderId: String,
    val cardType: String,
    val cardNo: String,
    val amount: String,
    val callbackUrl: String
)

data class PaymentResponse(
    val transactionKey: String,
    val orderId: String,
    val cardType: String,
    val cardNo: String,
    val amount: String,
    val status: String,
    val message: String?
)
