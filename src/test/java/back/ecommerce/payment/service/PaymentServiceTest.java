package back.ecommerce.payment.service;

import static org.mockito.BDDMockito.*;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import back.ecommerce.payment.entity.Payment;
import back.ecommerce.payment.repository.PaymentRepository;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {
	@Mock
	PaymentRepository paymentRepository;
	PaymentService paymentService;

	@BeforeEach
	void setUp() {
		paymentService = new PaymentService(paymentRepository);
	}

	@Test
	void create() {
		//given
		String orderCode = "55d89d0f-f2a9-4fc8-8c52-3b159b862698";
		String tId = "T543496451b66fd4x6dx1x5";

		//when
		Assertions.assertThatCode(() ->
				paymentService.createReadyPayment(tId, "TESCIDT", orderCode, "user@email.com", 500000L))
			.doesNotThrowAnyException();

		//then
		then(paymentRepository).should(times(1)).save(any(Payment.class));
	}
}