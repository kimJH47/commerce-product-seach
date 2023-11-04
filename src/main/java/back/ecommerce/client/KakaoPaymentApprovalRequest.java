package back.ecommerce.client;

import org.springframework.util.LinkedMultiValueMap;

public class KakaoPaymentApprovalRequest {

	private final String cid;
	private final String token;
	private final String transactionId;
	private final String orderCode;
	private final String userEmail;

	public KakaoPaymentApprovalRequest(String cid, String token, String transactionId, String orderCode,
		String userEmail) {
		this.cid = cid;
		this.token = token;
		this.transactionId = transactionId;
		this.orderCode = orderCode;
		this.userEmail = userEmail;
	}

	public LinkedMultiValueMap<String, String> toMap() {
		LinkedMultiValueMap<String, String> map = new LinkedMultiValueMap<>();
		map.add("cid",cid);
		map.add("tid",transactionId);
		map.add("partner_order_id",orderCode);
		map.add("partner_user_id",userEmail);
		map.add("pg_token",token);
		return map;
	}
}
