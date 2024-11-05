package back.ecommerce.cart.dto.response

import java.util.*

data class CartProducts(
    val count: Int,
    val totalPrice: Long,
    val value: List<CartProductDto>
) {
    companion object {
        fun create(list: List<CartProductDto>): CartProducts {
            val totalPrice = Collections.unmodifiableList(list).stream()
                .mapToLong(CartProductDto::price)
                .sum()
            return CartProducts(list.size, totalPrice, list)
        }
    }
}
