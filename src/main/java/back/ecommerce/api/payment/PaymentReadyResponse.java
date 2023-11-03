package back.ecommerce.api.payment;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class PaymentReadyResponse {
	private final String pcUrl;
	private final String orderCode;
	private final String createdAt;
}
