package back.ecommerce.api.payment;

import javax.validation.Valid;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import back.ecommerce.api.dto.Response;
import back.ecommerce.auth.annotaion.UserEmail;
import back.ecommerce.client.KakaoPaymentClient;
import back.ecommerce.order.service.OrderGroupDto;
import back.ecommerce.order.service.OrderService;
import back.ecommerce.payment.service.CancelPaymentDto;
import back.ecommerce.payment.service.PaymentDto;
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

	@PostMapping("/api/payment/ready")
	public ResponseEntity<?> paymentReady(@UserEmail String email, @Valid @RequestBody PaymentReadyRequest request) {
		OrderGroupDto orderGroupDto = orderService.createOrder(email, request.getTotalPrice(),
			request.getOrderProducts());
		KakaoReadyPaymentResult result = kakaoPaymentClient.ready(email, orderGroupDto.getOrderCode(),
			orderGroupDto.getTotalPrice(), orderGroupDto.getName(), orderGroupDto.getQuantity());
		paymentService.createReadyPayment(result.getTransactionId(), result.getCid(), result.getOrderCode(), email,
			request.getTotalPrice());
		return Response.createSuccessResponse("결제준비가 완료 되었습니다.",
			new PaymentReadyResponse(result.getPcUrl(), result.getOrderCode(), result.getCreatedAt()));
	}

	@GetMapping("/api/payment/callback-approval/{orderCode}")
	public ResponseEntity<?> approval(@PathVariable("orderCode") String orderCode,
		@RequestParam("pg_token") String token) {
		PaymentDto payment = paymentService.findByOrderCode(orderCode);
		kakaoPaymentClient.approval(token, payment.getTransactionId(), payment.getOrderCode(), payment.getUserEmail());
		paymentService.approval(orderCode);
		return ResponseEntity.status(HttpStatus.MOVED_PERMANENTLY)
			.header(HttpHeaders.LOCATION, "/")
			.build();
	}

	@GetMapping("/api/payment/callback-cancel/{orderCode}")
	public ResponseEntity<?> approvalCancel(@PathVariable("orderCode") String orderCode) {
		paymentService.cancel(orderCode);
		return ResponseEntity.status(HttpStatus.MOVED_PERMANENTLY)
			.header(HttpHeaders.LOCATION, "/")
			.build();
	}

	@PostMapping("/api/payment/approval-cancel")
	public ResponseEntity<?> approvalCancel(@RequestBody CancelPaymentRequest request) {
		CancelPaymentDto cancelPayment = paymentService.cancel(request.getOrderCode());
		kakaoPaymentClient.cancel(cancelPayment.getTransactionId(), request.getOrderCode(),
			cancelPayment.getTotalPrice());
		return ResponseEntity.status(HttpStatus.MOVED_PERMANENTLY)
			.header(HttpHeaders.LOCATION, "/")
			.build();
	}
}
