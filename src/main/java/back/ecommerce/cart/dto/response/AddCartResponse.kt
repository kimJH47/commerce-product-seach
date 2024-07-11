package back.ecommerce.cart.dto.response

data class AddCartResponse(
    val id: Long,
    val quantity: Int,
    val price: Long
)
