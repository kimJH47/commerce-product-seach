package back.ecommerce.config.logger;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import back.ecommerce.common.logging.GlobalLogger;

@Configuration
public class LoggerConfig {
	@Bean
	public GlobalLogger globalLogger() {
		return new GlobalLogger();
	}
}
