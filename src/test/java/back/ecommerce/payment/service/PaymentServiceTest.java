package back.ecommerce.payment.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import back.ecommerce.exception.CustomException;
import back.ecommerce.exception.ErrorCode;
import back.ecommerce.order.OrderGroupRepository;
import back.ecommerce.payment.entity.Payment;
import back.ecommerce.payment.entity.PaymentStatus;
import back.ecommerce.payment.repository.PaymentRepository;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {
	@Mock
	PaymentRepository paymentRepository;
	@Mock
	OrderGroupRepository orderGroupRepository;
	PaymentService paymentService;

	@BeforeEach
	void setUp() {
		paymentService = new PaymentService(paymentRepository, orderGroupRepository);
	}

	@Test
	void create() {
		//given
		String orderCode = "55d89d0f-f2a9-4fc8-8c52-3b159b862698";
		String tId = "T543496451b66fd4x6dx1x5";

		//when
		assertThatCode(() ->
				paymentService.createReadyPayment(tId, "TESCIDT", orderCode, "user@email.com", 500000L))
			.doesNotThrowAnyException();

		//then
		then(paymentRepository).should(times(1)).save(any(Payment.class));
	}

	@Test
	void findByOrderCode() {
		//given
		given(paymentRepository.findByOrderCode(anyString()))
			.willReturn(
				Optional.of(new Payment(10L, "tid", "cid", "email", "orderCode", PaymentStatus.CANCEL, 100000L, 100L)));

		//when
		PaymentDto actual = paymentService.findByOrderCode("order");

		//then
		assertThat(actual.getOrderCode()).isEqualTo("orderCode");
		assertThat(actual.getUserEmail()).isEqualTo("email");
		assertThat(actual.getTransactionId()).isEqualTo("tid");

		then(paymentRepository).should(times(1)).findByOrderCode(anyString());
	}

	@Test
	void findByOrderCode_Payment_not_found() {
	    //given
		given(paymentRepository.findByOrderCode(anyString()))
			.willThrow(new CustomException(ErrorCode.PAYMENT_NOT_FOUND));

	    //then
		assertThatThrownBy(() -> paymentService.findByOrderCode("orderCode"))
			.isInstanceOf(CustomException.class)
			.hasMessage("일치하는 결제정보가 없습니다.");

		then(paymentRepository).should(times(1)).findByOrderCode(anyString());
	}
}