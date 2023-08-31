package back.ecommerce.config.common;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import back.ecommerce.controller.common.GlobalLogger;

@Configuration
public class LoggerConfig {
	@Bean
	public GlobalLogger globalLogger() {
		return new GlobalLogger();
	}
}
