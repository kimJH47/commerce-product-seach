package back.ecommerce.order.entity;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import back.ecommerce.common.entity.BaseTimeEntity;

@Entity
@Table(name = "ORDER_GROUP")
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
	@OneToMany(mappedBy = "orderGroup",cascade = CascadeType.ALL)
	private List<OrderItem> orderItems;
}
