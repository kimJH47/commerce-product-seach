package back.ecommerce.payment.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import back.ecommerce.common.entity.BaseTimeEntity;

@Entity
public class Payment extends BaseTimeEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String transactionId;
	private String cid; //가맹점 번호
	private String userEmail;
	private String orderCode;
	private PaymentStatus paymentStatus;
	private Long totalPrice;
	private Long taxFreePrice;
}
