package back.ecommerce.order.entity;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

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

	public static OrderItem create(OrderProductDto orderProduct) {
		return new OrderItem(null, null,
			orderProduct.getProductId(),
			orderProduct.getName(),
			orderProduct.getPrice(),
			orderProduct.getQuantity());
	}
}
