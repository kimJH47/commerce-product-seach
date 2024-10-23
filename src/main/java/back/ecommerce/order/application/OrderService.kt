package back.ecommerce.order.application

import back.ecommerce.api.payment.OrderProductDto
import back.ecommerce.common.generator.ULIDGenerator
import back.ecommerce.exception.CustomException
import back.ecommerce.exception.ErrorCode
import back.ecommerce.order.OrderGroupRepository
import back.ecommerce.order.entity.OrderGroup
import back.ecommerce.order.service.OrderGroupDto
import back.ecommerce.order.service.OrderGroupDto.Companion.create
import back.ecommerce.product.entity.Product
import back.ecommerce.product.repository.ProductRepository
import back.ecommerce.user.repository.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class OrderService(
    private val orderGroupRepository: OrderGroupRepository,
    private val productRepository: ProductRepository,
    private val userRepository: UserRepository,
    private val ulidGenerator: ULIDGenerator
) {

    @Transactional
    fun createOrder(userEmail: String, totalPrice: Long, orderProducts: List<OrderProductDto>): OrderGroupDto {
        validateUserEmail(userEmail)
        val products = findByIds(orderProducts)
        validateOrder(orderProducts, products)
        val orderGroup = orderGroupRepository.save(createOrderGroup(userEmail, totalPrice, orderProducts))
        return create(orderGroup)
    }

    private fun findByIds(orderProducts: List<OrderProductDto>): List<Product> {
        return productRepository.findByIds(orderProducts
            .map { it.productId }
            .toList())
    }

    private fun validateUserEmail(userEmail: String) {
        if (!userRepository.existsByEmail(userEmail)) {
            throw CustomException(ErrorCode.USER_NOT_FOUND)
        }
    }

    private fun validateOrder(orderProducts: List<OrderProductDto>, products: List<Product>) {
        if (orderProducts.size != products.size) {
            throw CustomException(ErrorCode.INVALID_ORDER_ARGUMENT)
        }
        if (products.sumOf { it.price } != orderProducts.sumOf { it.price }) {
            throw CustomException(ErrorCode.INVALID_TOTAL_PRICE)
        }
    }

    private fun createOrderGroup(email: String, totalPrice: Long, products: List<OrderProductDto>): OrderGroup {
        val orderCode: String = ulidGenerator.generateULIDToUUID().toString()
        val quantity = calculateTotalQuantity(products)
        val name = createShortenName(products, quantity)
        return OrderGroup.createWithOrderItems(email, totalPrice, quantity, name, orderCode, products)
    }

    private fun createShortenName(products: List<OrderProductDto>, quantity: Int): String {
        if (products.size == 1) {
            return products[0].name
        }
        return String.format("%s 등 총 %s개의 상품", products[0].name, quantity)
    }

    private fun calculateTotalQuantity(products: List<OrderProductDto>): Int {
        return products.sumOf { it.quantity }
    }
}