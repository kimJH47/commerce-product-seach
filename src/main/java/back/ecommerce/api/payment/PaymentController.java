package back.ecommerce.api.payment;

import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import back.ecommerce.auth.annotaion.UserEmail;
import back.ecommerce.client.KakaoPaymentClient;
import back.ecommerce.order.service.OrderGroupDto;
import back.ecommerce.order.service.OrderService;
import back.ecommerce.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequiredArgsConstructor
@Slf4j
public class PaymentController {

	private final KakaoPaymentClient kakaoPaymentClient;
	private final PaymentService paymentService;
	private final OrderService orderService;

	/**
	 * 결제 요청 호출
	 */

	@PostMapping("/api/payment/ready")
	public ResponseEntity<?> paymentReady(@UserEmail String userEmail,
		@Valid @RequestBody PaymentReadyRequest request) {
		OrderGroupDto orderGroupDto = orderService.createOrder(userEmail, request.getTotalPrice(),
			request.getOrderProducts());
		KakaoReadyPaymentResult result = kakaoPaymentClient.ready(userEmail, orderGroupDto.getOrderCode(),
			orderGroupDto.getTotalPrice(), orderGroupDto.getName(), orderGroupDto.getQuantity());
		paymentService.createReadyPayment(result.getTransactionId(), result.getCid(), result.getOrderCode(), userEmail,
			request.getTotalPrice());
		return ResponseEntity.ok(result);
	}
}
