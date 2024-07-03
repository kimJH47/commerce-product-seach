package back.ecommerce.order.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import back.ecommerce.api.payment.OrderProductDto;
import back.ecommerce.common.entity.BaseTimeEntity;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "ORDER_ITEM")
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class OrderItem extends BaseTimeEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "order_group_id")
	private OrderGroup orderGroup;
	private Long productId;
	private String name;
	private Long price;
	private int quantity;

	public static OrderItem create(OrderGroup orderGroup, OrderProductDto orderProduct) {
		return new OrderItem(null, orderGroup,
			orderProduct.getProductId(),
			orderProduct.getName(),
			orderProduct.getPrice(),
			orderProduct.getQuantity());
	}
}
