package back.ecommerce.api.payment;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class KakaoReadyPaymentResult {
	private String appUrl;
	private String mobileUrl;
	private String pcUrl;
	private String cid;
	private String transactionId;
	private String orderCode;
	private String createdAt;
}
