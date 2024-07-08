package back.ecommerce.payment.service

import back.ecommerce.payment.entity.PaymentStatus

data class CancelPaymentDto(
    val transactionId: String,
    val totalPrice: Long,
    val paymentStatus: PaymentStatus
)