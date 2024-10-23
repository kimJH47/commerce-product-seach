package back.ecommerce.cart.dto.response

import back.ecommerce.cart.entity.Cart
import back.ecommerce.product.entity.Category

data class CartProductDto(
    val id: Long,
    val name: String,
    val brandName: String,
    val price: Long,
    val category: Category,
    val quantity: Int
) {
    companion object {
        fun create(cart: Cart): CartProductDto {
            val product = cart.product
            return CartProductDto(
                cart.id, product.name, product.brandName, cart.price, product.category,
                cart.quantity
            )
        }
    }
}
