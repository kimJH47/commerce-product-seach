package back.ecommerce.order.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import back.ecommerce.api.payment.OrderProductDto;
import back.ecommerce.common.entity.BaseTimeEntity;
import back.ecommerce.exception.CustomException;
import back.ecommerce.exception.ErrorCode;
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
	@OneToMany(mappedBy = "orderGroup", cascade = CascadeType.PERSIST)
	private List<OrderItem> orderItems = new ArrayList<>();

	public Long getId() {
		return id;
	}

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

	public static OrderGroup createWithOrderItems(String userEmail, Long totalPrice, int totalQuantity, String name,
		String orderCode,
		List<OrderProductDto> orderProducts) {
		OrderGroup orderGroup = createOrderGroupWithEmptyOrderItems(userEmail, totalPrice, totalQuantity, name,
			orderCode);
		List<OrderItem> items = orderProducts.stream()
			.map(p -> OrderItem.create(orderGroup, p))
			.collect(Collectors.toList());
		orderGroup.addOrderItem(items);
		return orderGroup;
	}

	private static OrderGroup createOrderGroupWithEmptyOrderItems(String userEmail, Long totalPrice, int totalQuantity,
		String name, String orderCode) {
		return OrderGroup.builder()
			.orderCode(orderCode)
			.name(name)
			.userEmail(userEmail)
			.totalPrice(totalPrice)
			.quantity(totalQuantity)
			.orderStatus(OrderStatus.PAYMENT_READY)
			.orderItems(new ArrayList<>())
			.build();
	}

	private void addOrderItem(List<OrderItem> orderItems) {
		this.orderItems = new ArrayList<>(orderItems);
	}

	public void updateToPaymentApproval() {
		if (isNotPaymentReady()) {
			throw new CustomException(ErrorCode.ALREADY_PROCESS_ORDER);
		}
		orderStatus = OrderStatus.PAYMENT_APPROVAL;
	}

	private boolean isNotPaymentReady() {
		return !orderStatus.equals(OrderStatus.PAYMENT_READY);
	}

	public void cancel() {
		if (OrderStatus.cancellable(orderStatus)) {
			orderStatus = OrderStatus.ORDER_CANCEL;
			return;
		}
		throw new CustomException(ErrorCode.ALREADY_PROCESS_ORDER);
	}

	public boolean isSamePrice(Long cancelPrice) {
		return Objects.equals(this.totalPrice, cancelPrice);
	}
}
