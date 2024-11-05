package back.ecommerce.cart.dto.response

data class CartListResponse(
    val email: String,
    val cartProducts: CartProducts
)

