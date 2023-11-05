package back.ecommerce.client;

import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

public class KakaoPaymentCancelRequest {

	private final String transactionId;
	private final String orderCode;
	private final Long totalPrice;

	public KakaoPaymentCancelRequest(String transactionId, String orderCode, Long totalPrice) {
		this.transactionId = transactionId;
		this.orderCode = orderCode;
		this.totalPrice = totalPrice;
	}

	public MultiValueMap<String, String> toMap() {
		LinkedMultiValueMap<String, String> map = new LinkedMultiValueMap<>();

		return map;
	}
}
