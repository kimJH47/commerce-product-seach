package back.ecommerce.api;

import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import back.ecommerce.api.auth.interceptor.JwtAuthenticationInterceptor;
import back.ecommerce.auth.service.TokenExtractor;
import back.ecommerce.auth.service.TokenProvider;
import back.ecommerce.client.KakaoPaymentClient;
import back.ecommerce.common.logging.GlobalLogger;
import back.ecommerce.admin.repository.AdminRepository;

@TestConfiguration
public class MockMvcTestConfig {

	@Bean
	public TokenProvider tokenProvider() {
		return Mockito.mock(TokenProvider.class);
	}

	@Bean
	public GlobalLogger globalLogger() {
		return new GlobalLogger();
	}

	@Bean
	public AdminRepository adminRepository() {
		return Mockito.mock(AdminRepository.class);
	}

	@Bean
	public KakaoPaymentClient kakaoPaymentClient() {
		return Mockito.mock(KakaoPaymentClient.class);
	}

	@Bean
	public JwtAuthenticationInterceptor jwtAuthenticationInterceptor() {
		return Mockito.mock(JwtAuthenticationInterceptor.class);
	}

	@Bean
	public TokenExtractor tokenExtractor() {
		return Mockito.mock(TokenExtractor.class);
	}
}
