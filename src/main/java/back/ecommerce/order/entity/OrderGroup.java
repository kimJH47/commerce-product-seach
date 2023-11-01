package back.ecommerce.order.entity;

import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import back.ecommerce.api.payment.OrderProductDto;
import back.ecommerce.common.entity.BaseTimeEntity;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "ORDER_GROUP")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class OrderGroup extends BaseTimeEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String orderCode;
	private String name;
	private String userEmail;
	private Long totalPrice;
	private int quantity;
	@Enumerated(EnumType.STRING)
	private OrderStatus orderStatus;
	@OneToMany(mappedBy = "orderGroup", cascade = CascadeType.ALL)
	private List<OrderItem> orderItems;

	public String getOrderCode() {
		return orderCode;
	}

	public Long getTotalPrice() {
		return totalPrice;
	}

	public String getName() {
		return name;
	}

	public int getQuantity() {
		return quantity;
	}

	public static OrderGroup createWithOrderItems(String userEmail, Long totalPrice, String name, String orderCode,
		List<OrderProductDto> orderProducts) {
		List<OrderItem> items = orderProducts.stream()
			.map(OrderItem::create)
			.collect(Collectors.toList());
		return OrderGroup.builder()
			.orderCode(orderCode)
			.name(name)
			.userEmail(userEmail)
			.totalPrice(totalPrice)
			.quantity(items.size())
			.orderStatus(OrderStatus.PAYMENT_READY)
			.orderItems(items)
			.build();
	}

}
