package back.ecommerce.payment.entity;

import static back.ecommerce.payment.entity.PaymentStatus.*;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import back.ecommerce.common.entity.BaseTimeEntity;
import back.ecommerce.exception.CustomException;
import back.ecommerce.exception.ErrorCode;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Payment extends BaseTimeEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String transactionId;
	private String cid;
	private String userEmail;
	private String orderCode;
	@Enumerated(EnumType.STRING)
	private PaymentStatus paymentStatus;
	private Long totalPrice;
	private Long taxFreePrice;

	public static Payment createReadyPayment(String transactionId, String cid, String orderCode, String userEmail,
		Long totalPrice) {
		return new Payment(null, transactionId, cid, userEmail, orderCode, READY, totalPrice, 0L);
	}

	public void updatePaymentStatus(PaymentStatus paymentStatus) {
		if (paymentStatus.equals(APPROVAL)) {
			updateApproval();
			return;
		}
		if (paymentStatus.equals(FAIL)) {
			updateFail();
			return;
		}
		if (paymentStatus.equals(CANCEL)) {
			updateCancel();
			return;
		}
		throw new CustomException(ErrorCode.ALREADY_PROCESS_PAYMENT);
	}

	private void updateApproval() {
		if (isNotReady()) {
			throw new CustomException(ErrorCode.ALREADY_PROCESS_PAYMENT);
		}
		paymentStatus = APPROVAL;
	}

	private void updateFail() {
		if (isNotReady()) {
			throw new CustomException(ErrorCode.ALREADY_PROCESS_PAYMENT);
		}
		paymentStatus = FAIL;
	}

	private boolean isNotReady() {
		return !paymentStatus.equals(READY);
	}

	private void updateCancel() {
		if (paymentStatus.equals(CANCEL) || paymentStatus.equals(FAIL)) {
			throw new CustomException(ErrorCode.ALREADY_PROCESS_PAYMENT);
		}
		paymentStatus = CANCEL;
	}

}
