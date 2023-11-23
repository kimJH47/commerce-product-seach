package back.ecommerce.integration;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class TestAwsConfig {

	@Bean
	public TestEmailPublisher emailPublisher() {
		return new TestEmailPublisher();
	}
}
