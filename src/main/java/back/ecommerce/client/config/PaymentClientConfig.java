package back.ecommerce.client.config;

import static com.amazonaws.services.s3.Headers.*;
import static org.springframework.http.MediaType.*;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

import back.ecommerce.client.KakaoPaymentClient;

@Configuration
public class PaymentClientConfig {
	@Value("kakao.payment.adminKey")
	private String adminKey;
	@Value("kakao.payment.cid")
	private String cid;
	@Value("kakao.payment.approvalUrl")
	private String approvalUrl;
	@Value("kakao.payment.cancelUrl")
	private String cancelUrl;
	@Value("kakao.payment.failUrl")
	private String failUrl;

	@Bean
	public KakaoPaymentClient kakaoPaymentClient() {
		return new KakaoPaymentClient(adminKey, cid, approvalUrl, cancelUrl, failUrl, webClient());
	}

	@Bean
	public WebClient webClient() {
		return WebClient.builder()
			.baseUrl("https://kapi.kakao.com/v1/payment")
			.defaultHeader(CONTENT_TYPE, APPLICATION_FORM_URLENCODED_VALUE)
			.build();
	}
}
