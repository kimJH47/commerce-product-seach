package back.ecommerce.config.aop;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

import back.ecommerce.aop.aspect.LoggingAspect;
import back.ecommerce.common.logging.SearchRequestLogger;

@Configuration
@EnableAspectJAutoProxy
public class AopConfig {

	@Bean
	public LoggingAspect loggingAspect() {
		return new LoggingAspect(new SearchRequestLogger());
	}
}
