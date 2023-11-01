package back.ecommerce.order.service;

import back.ecommerce.order.entity.OrderGroup;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class OrderGroupDto {
	private final String orderCode;
	private final String name;
	private final Integer quantity;
	private final Long totalPrice;

	public static OrderGroupDto create(OrderGroup orderGroup) {
		return new OrderGroupDto(orderGroup.getOrderCode(), orderGroup.getName(), orderGroup.getQuantity(),
			orderGroup.getTotalPrice());
	}
}
