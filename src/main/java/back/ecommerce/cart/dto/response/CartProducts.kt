package back.ecommerce.cart.dto.response

import java.util.*

data class CartProducts(
    val count: Int,
    val totalPrice: Long,
    val value: List<CartProductDto>
) {
    companion object {
        fun create(list: List<CartProductDto>): CartProducts {
            val unmodifiableList = Collections.unmodifiableList(list)
            val totalPrice = unmodifiableList.stream()
                .mapToLong { it.price * it.quantity }
                .sum()
            val totalCount = unmodifiableList.stream()
                .mapToInt {
                    it.quantity
                }.sum()
            return CartProducts(totalCount, totalPrice, list)
        }
    }
}
