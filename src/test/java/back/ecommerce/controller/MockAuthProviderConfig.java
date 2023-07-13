package back.ecommerce.controller;

import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import back.ecommerce.auth.token.TokenProvider;

@TestConfiguration
public class MockAuthProviderConfig {

	@Bean
	public TokenProvider tokenProvider() {
		return Mockito.mock(TokenProvider.class);
	}
}
