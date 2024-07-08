package back.ecommerce.order.service;

import back.ecommerce.order.entity.OrderGroup;

public record OrderGroupDto(String orderCode, String name, Integer quantity, Long totalPrice) {
	public static OrderGroupDto create(OrderGroup orderGroup) {
		return new OrderGroupDto(orderGroup.getOrderCode(), orderGroup.getName(), orderGroup.getQuantity(),
			orderGroup.getTotalPrice());
	}

}
