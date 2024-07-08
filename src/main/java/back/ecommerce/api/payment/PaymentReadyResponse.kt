package back.ecommerce.api.payment

data class PaymentReadyResponse(val pcUrl: String, val orderCode: String, val createdAt: String)
