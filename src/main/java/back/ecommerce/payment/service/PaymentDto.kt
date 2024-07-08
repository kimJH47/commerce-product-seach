package back.ecommerce.payment.service;

import lombok.Getter;

@Getter
public class PaymentDto {

	private final String transactionId;
	private final String orderCode;
	private final String userEmail;

	public PaymentDto(String transactionId, String orderCode, String userEmail) {
		this.transactionId = transactionId;
		this.orderCode = orderCode;
		this.userEmail = userEmail;
	}
}
