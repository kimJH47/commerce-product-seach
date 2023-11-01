package back.ecommerce.order.entity;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import back.ecommerce.common.entity.BaseTimeEntity;

@Entity
@Table(name = "ORDER_ITEM")
public class OrderItem extends BaseTimeEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "order_group_id")
	private OrderGroup orderGroup;
	private String name;
	private Long price;
	private int quantity;
}
