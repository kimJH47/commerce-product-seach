package back.ecommerce.config.aop;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

import back.ecommerce.aop.LoggingAspect;
import back.ecommerce.controller.common.SearchRequestLogger;

@Configuration
@EnableAspectJAutoProxy
public class AopConfig {

	@Bean
	public LoggingAspect loggingAspect() {
		return new LoggingAspect(new SearchRequestLogger());
	}
}
