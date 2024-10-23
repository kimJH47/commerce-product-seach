package back.ecommerce.cart.dto.response

data class CartListResponse(
    private val email: String,
    private val cartProducts: CartProducts
)

