package back.ecommerce.payment.service;

import org.springframework.stereotype.Service;

import back.ecommerce.payment.entity.Payment;
import back.ecommerce.payment.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PaymentService {
	private final PaymentRepository paymentRepository;

	public void createReadyPayment(String transactionId, String cid, String orderCode, String userEmail,
		Long totalPrice) {
		paymentRepository.save(Payment.createReadyPayment(transactionId, cid, orderCode, userEmail, totalPrice));
	}
}
