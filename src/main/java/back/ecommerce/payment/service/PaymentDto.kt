package back.ecommerce.payment.service


data class PaymentDto(
    val transactionId: String,
    val orderCode: String,
    val userEmail: String
)
