package back.ecommerce.order.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum OrderStatus {
	ORDER_FAILED("주문실패"),
	ORDER_CANCEL("주문 취소"),
	PAYMENT_READY("결제준비"),
	PAYMENT_APPROVAL("결제 승인"),
	DELIVERY_READY("배송 준비"),
	DELIVERY("배송중"),
	DELIVERY_SUCCEED("배송 성공"),
	PAYMENT_FAILED("결제 실패");

	private final String description;

	public static boolean cancellable(OrderStatus orderStatus) {
		return orderStatus == PAYMENT_READY || orderStatus == PAYMENT_APPROVAL || orderStatus == DELIVERY_READY;
	}
}
