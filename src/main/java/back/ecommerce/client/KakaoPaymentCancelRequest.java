package back.ecommerce.client;

import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

public class KakaoPaymentCancelRequest {

	private final String cid;
	private final String transactionId;
	private final String orderCode;
	private final Long totalPrice;

	public KakaoPaymentCancelRequest(String cid, String transactionId, String orderCode, Long totalPrice) {
		this.cid = cid;
		this.transactionId = transactionId;
		this.orderCode = orderCode;
		this.totalPrice = totalPrice;
	}

	public MultiValueMap<String, String> toMap() {
		LinkedMultiValueMap<String, String> map = new LinkedMultiValueMap<>();
		map.add("cid", cid);
		map.add("tid", transactionId);
		map.add("cancel_amount", totalPrice.toString());
		map.add("cancel_tax_free_amount", "0");
		return map;
	}
}
