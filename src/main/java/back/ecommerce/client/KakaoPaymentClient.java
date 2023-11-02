package back.ecommerce.client;

import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import back.ecommerce.api.payment.KakakoPaymentReadyResponse;
import back.ecommerce.api.payment.KakaoReadyPaymentResult;
import back.ecommerce.api.payment.KakaoPaymentReadyRequest;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class KakaoPaymentClient {

	private final String adminKey;
	private final String cid;
	private final String approvalUrl;
	private final String cancelUrl;
	private final String failUrl;
	private final WebClient webClient;

	public KakaoReadyPaymentResult ready(String userEmail, String orderCode, Long totalPrice, String name,
		Integer quantity) {
		KakaoPaymentReadyRequest request = new KakaoPaymentReadyRequest(
			cid, orderCode, userEmail, name, quantity, totalPrice, 0L, approvalUrl, cancelUrl, failUrl
		);
		KakakoPaymentReadyResponse response = webClient.post()
			.uri("/ready")
			.header(HttpHeaders.AUTHORIZATION, adminKey)
			.body(BodyInserters.fromFormData(request.toMap()))
			.retrieve()
			.bodyToMono(KakakoPaymentReadyResponse.class)
			.block();
		assert response != null;
		return new KakaoReadyPaymentResult(response.getNext_redirect_app_url(),
			response.getNext_redirect_mobile_url(), response.getNext_redirect_pc_url(),
			cid, response.getTid(), orderCode);
	}

}
