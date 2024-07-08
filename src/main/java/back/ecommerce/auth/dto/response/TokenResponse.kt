package back.ecommerce.auth.dto.response

data class TokenResponse(
    val accessToken: String,
    val expireTime: Long,
    val type: String

)