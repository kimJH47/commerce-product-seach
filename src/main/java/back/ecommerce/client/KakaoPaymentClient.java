package back.ecommerce.client;

import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import back.ecommerce.api.payment.KakaoPaymentReadyResponse;
import back.ecommerce.api.payment.KakaoPaymentReadyRequest;
import back.ecommerce.api.payment.KakaoReadyPaymentResult;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class KakaoPaymentClient {

	private final String cid;
	private final String approvalUrl;
	private final String cancelUrl;
	private final String failUrl;
	private final WebClient webClient;

	public KakaoReadyPaymentResult ready(String userEmail, String orderCode, Long totalPrice, String name,
		Integer quantity) {
		KakaoPaymentReadyRequest request = createKakaoPaymentReadyRequest(userEmail, orderCode, totalPrice, name,
			quantity);
		KakaoPaymentReadyResponse response = getKakaoPaymentReadyResponse(request);
		assert response != null;
		return new KakaoReadyPaymentResult(response.getNext_redirect_app_url(),
			response.getNext_redirect_mobile_url(), response.getNext_redirect_pc_url(),
			cid, response.getTid(), orderCode, response.getCreated_at());
	}

	private KakaoPaymentReadyRequest createKakaoPaymentReadyRequest(String userEmail, String orderCode, Long totalPrice,
		String name, Integer quantity) {
		return new KakaoPaymentReadyRequest(
			cid, orderCode, userEmail, name, quantity, totalPrice, 0L, approvalUrl + "/" + orderCode,
			cancelUrl + "/" + orderCode, failUrl + "/" + orderCode);
	}

	private KakaoPaymentReadyResponse getKakaoPaymentReadyResponse(KakaoPaymentReadyRequest request) {
		return webClient.post()
			.uri("/ready")
			.body(BodyInserters.fromFormData(request.toMap()))
			.retrieve()
			.bodyToMono(KakaoPaymentReadyResponse.class)
			.block();
	}

	public KakaoPaymentApprovalResult approval(String token, String transactionId, String orderCode, String userEmail) {
		KakaoPaymentApprovalRequest request = new KakaoPaymentApprovalRequest(cid, token, transactionId, orderCode,
			userEmail);
		return webClient.post()
			.uri("/approve")
			.body(BodyInserters.fromFormData(request.toMap()))
			.retrieve()
			.bodyToMono(KakaoPaymentApprovalResult.class)
			.block();
	}

	public KakaoPaymentCancelResult cancel(String transactionId, String orderCode, Long totalPrice) {
		KakaoPaymentCancelRequest request = new KakaoPaymentCancelRequest(transactionId, orderCode,
			totalPrice);
		return webClient.post()
			.uri("/cancel")
			.body(BodyInserters.fromFormData(request.toMap()))
			.retrieve()
			.bodyToMono(KakaoPaymentCancelResult.class)
			.block();
	}
}
