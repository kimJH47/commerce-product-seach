package back.ecommerce.api.payment;

import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import com.fasterxml.jackson.databind.ObjectMapper;

import back.ecommerce.api.MockMvcTestConfig;
import back.ecommerce.api.common.GlobalExceptionHandler;
import back.ecommerce.auth.annotaion.UserEmail;
import back.ecommerce.client.KakaoPaymentClient;
import back.ecommerce.common.logging.GlobalLogger;
import back.ecommerce.exception.CustomException;
import back.ecommerce.exception.ErrorCode;
import back.ecommerce.order.service.OrderGroupDto;
import back.ecommerce.order.service.OrderService;
import back.ecommerce.payment.service.PaymentService;

@WebMvcTest(PaymentController.class)
@Import(MockMvcTestConfig.class)
class PaymentControllerTest {

	@MockBean
	KakaoPaymentClient kakaoPaymentClient;
	@MockBean
	PaymentService paymentService;
	@MockBean
	OrderService orderService;
	MockMvc mvc;
	@Autowired
	ObjectMapper mapper;

	@BeforeEach
	void setUp() {
		mvc = MockMvcBuilders.standaloneSetup(new PaymentController(kakaoPaymentClient, paymentService, orderService))
			.setCustomArgumentResolvers(new MockUserEmailArgumentResolver())
			.setControllerAdvice(new GlobalExceptionHandler(new GlobalLogger()))
			.build();
	}

	@Test
	void paymentReady() throws Exception {
		//given
		String orderCode = "d20bfd22-bf3d-4d63-a616-831610627a05";
		OrderGroupDto orderGroupDto = new OrderGroupDto(orderCode, "Embroidered Maxi Dress 등 총 2개의 상품", 2, 132130L);
		String createdAt = LocalDateTime.now().toString();
		KakaoReadyPaymentResult result = new KakaoReadyPaymentResult(
			"https://online-pay.kakao.com/test/v1/edc130cb9b44f2fe56dd928ac05c1fddf3f79a2351fc79d642b457695cb49f81/aInfo",
			"https://online-pay.kakao.com/mockup/v1/edc130cb9b44f2fe56dd928ac05c1fddf3f79a2351fc79d642b457695cb49f81/mInfo",
			"https://online-pay.kakao.com/mockup/v1/edc130cb9b44f2fe56dd928ac05c1fddf3f79a2351fc79d642b457695cb49f81/info",
			"TC0ONETIME",
			"T543457051b66fd46cc1vb", orderCode, createdAt);
		List<OrderProductDto> orderProducts = getOrderProducts();
		PaymentReadyRequest request = new PaymentReadyRequest("user@email.com", 3L, 500000L, orderProducts);
		given(orderService.createOrder(anyString(), anyLong(), anyList()))
			.willReturn(orderGroupDto);
		given(kakaoPaymentClient.ready(anyString(), anyString(), anyLong(), anyString(), anyInt())).
			willReturn(result);

		//expect
		mvc.perform(post("/api/payment/ready")
				.contentType(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsString(request)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.message").value("결제준비가 완료 되었습니다."))
			.andExpect(jsonPath("$.entity.pcUrl").value(
				"https://online-pay.kakao.com/mockup/v1/edc130cb9b44f2fe56dd928ac05c1fddf3f79a2351fc79d642b457695cb49f81/info"))
			.andExpect(jsonPath("$.entity.orderCode").value("d20bfd22-bf3d-4d63-a616-831610627a05"))
			.andExpect(jsonPath("$.entity.createdAt").value(createdAt));

		then(orderService).should(times(1))
			.createOrder(anyString(), anyLong(), anyList());
		then(kakaoPaymentClient).should(times(1))
			.ready(anyString(), anyString(), anyLong(), anyString(), anyInt());
		then(paymentService).should(times(1))
			.createReadyPayment(anyString(), anyString(), anyString(), anyString(), anyLong());
	}

	@Test
	void payment_ready_user_not_found() throws Exception {
		//given
		PaymentReadyRequest request = new PaymentReadyRequest("user@email.com", 3L, 500000L, getOrderProducts());

		given(orderService.createOrder(anyString(), anyLong(), anyList()))
			.willThrow(new CustomException(ErrorCode.USER_NOT_FOUND));

		//expect
		mvc.perform(post("/api/payment/ready")
				.contentType(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsString(request)))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.message").value("잘못된 요청입니다."))
			.andExpect(jsonPath("$.reasons.user").value("해당하는 유저가 존재하지 않습니다."));

		then(orderService).should(times(1))
			.createOrder(anyString(), anyLong(), anyList());
		then(kakaoPaymentClient).should(times(0))
			.ready(anyString(), anyString(), anyLong(), anyString(), anyInt());
		then(paymentService).should(times(0))
			.createReadyPayment(anyString(), anyString(), anyString(), anyString(), anyLong());
	}

	@Test
	void payment_ready_invalid_order_argument() throws Exception {
		//given
		PaymentReadyRequest request = new PaymentReadyRequest("user@email.com", 3L, 500000L, getOrderProducts());

		given(orderService.createOrder(anyString(), anyLong(), anyList()))
			.willThrow(new CustomException(ErrorCode.INVALID_ORDER_ARGUMENT));

		//expect
		mvc.perform(post("/api/payment/ready")
				.contentType(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsString(request)))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.message").value("잘못된 요청입니다."))
			.andExpect(jsonPath("$.reasons.order").value("주문정보가 옳바르지 않습니다."));

		then(orderService).should(times(1))
			.createOrder(anyString(), anyLong(), anyList());
		then(kakaoPaymentClient).should(times(0))
			.ready(anyString(), anyString(), anyLong(), anyString(), anyInt());
		then(paymentService).should(times(0))
			.createReadyPayment(anyString(), anyString(), anyString(), anyString(), anyLong());
	}

	@Test
	void payment_ready_INVALID_TOTAL_PRICE() throws Exception {
		//given
		PaymentReadyRequest request = new PaymentReadyRequest("user@email.com", 3L, 500000L, getOrderProducts());

		given(orderService.createOrder(anyString(), anyLong(), anyList()))
			.willThrow(new CustomException(ErrorCode.INVALID_TOTAL_PRICE));

		//expect
		mvc.perform(post("/api/payment/ready")
				.contentType(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsString(request)))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.message").value("잘못된 요청입니다."))
			.andExpect(jsonPath("$.reasons.order").value("요청 주문상품가격과 실제 가격정보가 일치하지 않습니다."));

		then(orderService).should(times(1))
			.createOrder(anyString(), anyLong(), anyList());
		then(kakaoPaymentClient).should(times(0))
			.ready(anyString(), anyString(), anyLong(), anyString(), anyInt());
		then(paymentService).should(times(0))
			.createReadyPayment(anyString(), anyString(), anyString(), anyString(), anyLong());
	}

	private OrderProductDto createOrderDto(long productId, String name, int quantity, long price) {
		return new OrderProductDto(productId, name, quantity, price);
	}

	private List<OrderProductDto> getOrderProducts() {
		List<OrderProductDto> orderProducts = new ArrayList<>();
		orderProducts.add(createOrderDto(1L, "name1", 1, 250000L));
		orderProducts.add(createOrderDto(10L, "name2", 2, 250000L));
		return orderProducts;
	}

	static class MockUserEmailArgumentResolver implements HandlerMethodArgumentResolver {

		@Override
		public boolean supportsParameter(MethodParameter parameter) {
			return parameter.getParameterAnnotation(UserEmail.class) != null
				&& parameter.getParameterType().equals(String.class);
		}

		@Override
		public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
			NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
			return "user@email.com";
		}
	}
}