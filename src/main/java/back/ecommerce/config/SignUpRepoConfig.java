package back.ecommerce.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import back.ecommerce.user.repository.SignUpMysqlRepository;
import back.ecommerce.user.repository.SignUpRepository;
import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class SignUpRepoConfig {

	private final SignUpMysqlRepository signUpMysqlRepository;

	@Bean
	public SignUpRepository signUpRepository() {
		return signUpMysqlRepository;
	}
}
