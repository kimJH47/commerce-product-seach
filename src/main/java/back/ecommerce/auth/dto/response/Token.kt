package back.ecommerce.auth.dto.response

data class Token(val value: String, val expireTime: Int, val type: String)
