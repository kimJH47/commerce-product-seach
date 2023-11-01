package back.ecommerce.order.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum OrderStatus {
	ORDER_FAILED("주문실패"),
	PAYMENT_READY("결제준비"),
	DELIVERY("배송중"),
	DELIVERY_SUCCEED("배송 성공"),
	PAYMENT_FAILED("결제 실패");

	private final String description;
}
