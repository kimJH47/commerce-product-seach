package back.ecommerce.payment.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import back.ecommerce.exception.CustomException;
import back.ecommerce.exception.ErrorCode;
import back.ecommerce.payment.entity.Payment;
import back.ecommerce.payment.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PaymentService {
	private final PaymentRepository paymentRepository;

	@Transactional
	public void createReadyPayment(String transactionId, String cid, String orderCode, String userEmail,
		Long totalPrice) {
		paymentRepository.save(Payment.createReadyPayment(transactionId, cid, orderCode, userEmail, totalPrice));
	}

	@Transactional(readOnly = true)
	public PaymentDto findByOrderCode(String orderCode) {
		Payment payment = paymentRepository.findByOrderCode(orderCode)
			.orElseThrow(() -> new CustomException(ErrorCode.PAYMENT_NOT_FOUND));
		validatePayment(payment);
		return new PaymentDto(payment.getTransactionId(), payment.getOrderCode(), payment.getUserEmail());
	}

	private void validatePayment(Payment payment) {
		if (payment.isNotReady()) {
			throw new CustomException(ErrorCode.ALREADY_PROCESS_PAYMENT);
		}
	}
}
