package back.ecommerce.payment.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import back.ecommerce.exception.CustomException;
import back.ecommerce.exception.ErrorCode;
import back.ecommerce.order.OrderGroupRepository;
import back.ecommerce.payment.entity.Payment;
import back.ecommerce.payment.entity.PaymentStatus;
import back.ecommerce.payment.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PaymentService {
	private final PaymentRepository paymentRepository;
	private final OrderGroupRepository orderGroupRepository;

	@Transactional
	public void createReadyPayment(String transactionId, String cid, String orderCode, String userEmail,
		Long totalPrice) {
		paymentRepository.save(Payment.createReadyPayment(transactionId, cid, orderCode, userEmail, totalPrice));
	}

	@Transactional(readOnly = true)
	public PaymentDto findByOrderCode(String orderCode) {
		Payment payment = findByPaymentWithOrderCode(orderCode);
		return new PaymentDto(payment.getTransactionId(), payment.getOrderCode(), payment.getUserEmail());
	}

	@Transactional
	public void approval(String orderCode) {
		findByPaymentWithOrderCode(orderCode)
			.updatePaymentStatus(PaymentStatus.APPROVAL);
		orderGroupRepository.findByOrderCode(orderCode)
			.orElseThrow(() -> new CustomException(ErrorCode.ORDER_NOT_FOUND))
			.updateToDelivery();
	}

	private Payment findByPaymentWithOrderCode(String orderCode) {
		return paymentRepository.findByOrderCode(orderCode)
			.orElseThrow(() -> new CustomException(ErrorCode.PAYMENT_NOT_FOUND));
	}
}
