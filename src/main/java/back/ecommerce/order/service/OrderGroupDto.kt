package back.ecommerce.order.service

import back.ecommerce.order.entity.OrderGroup

data class OrderGroupDto(
    val orderCode: String,
    val name: String,
    val quantity: Int,
    val totalPrice: Long
) {
    companion object {
        fun create(orderGroup: OrderGroup): OrderGroupDto {
            return OrderGroupDto(
                orderGroup.orderCode, orderGroup.name, orderGroup.quantity,
                orderGroup.totalPrice
            )
        }
    }
}
