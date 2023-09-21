package back.ecommerce.controller;

import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import back.ecommerce.auth.token.TokenProvider;
import back.ecommerce.common.logging.GlobalLogger;
import back.ecommerce.repository.admin.AdminRepository;

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
}
