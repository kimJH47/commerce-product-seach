package back.ecommerce.payment.repository;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import back.ecommerce.payment.entity.Payment;
import back.ecommerce.payment.entity.PaymentStatus;

@DataJpaTest
class PaymentRepositoryTest {

	@Autowired
	PaymentRepository paymentRepository;

	@BeforeEach
	void setUp() {
		paymentRepository.save(new Payment(
			null, "tid", "cid", "email", "orderCode1", PaymentStatus.FAIL,
			10000L, 100L));
		paymentRepository.save(new Payment(
			null, "tid1", "cid2", "email", "orderCode2", PaymentStatus.FAIL,
			10000L, 100L));
		paymentRepository.save(new Payment(
			null, "tid", "cid", "email", "orderCode3", PaymentStatus.APPROVAL,
			10000L, 100L));
	}

	@Test
	void findByOrderCode() {
	    //given
		paymentRepository.save(new Payment(
			null, "tid", "cid5", "email", "orderCode5", PaymentStatus.APPROVAL, 10000L, 100L));
	    //when
		Payment actual = paymentRepository.findByOrderCode("orderCode5")
			.get();

		//then
		Assertions.assertThat(actual.getCid()).isEqualTo("cid5");
		Assertions.assertThat(actual.getOrderCode()).isEqualTo("orderCode5");

	}
}