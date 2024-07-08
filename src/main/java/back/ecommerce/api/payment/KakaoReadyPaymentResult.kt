package back.ecommerce.api.payment

data class KakaoReadyPaymentResult(
    val appUrl: String,
    val mobileUrl: String,
    val pcUrl: String,
    val cid: String,
    val transactionId: String,
    val orderCode: String,
    val createdAt: String,
)