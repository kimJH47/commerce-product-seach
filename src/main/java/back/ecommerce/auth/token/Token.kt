package back.ecommerce.auth.token

data class Token(val value: String, val expireTime: Int, val type: String)
