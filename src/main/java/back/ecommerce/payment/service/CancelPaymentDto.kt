package back.ecommerce.payment.service;

import back.ecommerce.payment.entity.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CancelPaymentDto {
	private final String transactionId;
	private final Long totalPrice;
	private final PaymentStatus paymentStatus;
}
