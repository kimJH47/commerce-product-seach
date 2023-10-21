package back.ecommerce.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import back.ecommerce.repository.user.SignUpMysqlRepository;
import back.ecommerce.repository.user.SignUpRepository;
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
