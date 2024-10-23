package back.ecommerce.cart.application

import back.ecommerce.cart.dto.response.*
import back.ecommerce.cart.entity.Cart
import back.ecommerce.cart.repository.CartRepository
import back.ecommerce.exception.CustomException
import back.ecommerce.exception.ErrorCode
import back.ecommerce.product.repository.ProductRepository
import back.ecommerce.user.repository.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class CartService(
    private val cartRepository: CartRepository,
    private val userRepository: UserRepository,
    private val productRepository: ProductRepository
) {

    @Transactional
    fun addProduct(email: String, productId: Long, quantity: Int): AddCartResponse {
        validateUserEmail(email)
        val product = productRepository.findById(productId)
            .orElseThrow { CustomException(ErrorCode.PRODUCT_NOT_FOUND) }
        val cart = cartRepository.save(Cart.create(email, product, quantity))
        return AddCartResponse(cart.id, cart.quantity, cart.price)
    }

    @Transactional(readOnly = true)
    fun findCartByUserEmail(email: String): CartListResponse {
        validateUserEmail(email)
        val cartProducts = cartRepository.findByUserEmail(email)
            .map { CartProductDto.create(it) }
            .toList()

        return CartListResponse(email, CartProducts.create(cartProducts))
    }

    private fun validateUserEmail(email: String) {
        if (!userRepository.existsByEmail(email)) {
            throw CustomException(ErrorCode.USER_NOT_FOUND)
        }
    }

    @Transactional
    fun deleteById(cartId: Long, email: String): CartDeleteResponse {
        validateUserEmail(email)
        cartRepository.deleteById(cartId)
        return CartDeleteResponse(email, cartId)
    }
}