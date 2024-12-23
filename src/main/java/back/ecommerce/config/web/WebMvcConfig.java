package back.ecommerce.config.web;

import java.util.List;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import back.ecommerce.api.resolver.SearchConditionRequestResolver;
import back.ecommerce.api.auth.interceptor.AdminAuthorizationInterceptor;
import back.ecommerce.api.auth.interceptor.JwtAuthenticationInterceptor;
import back.ecommerce.api.auth.resolver.UserEmailArgumentResolver;
import back.ecommerce.common.logging.GlobalLogger;
import back.ecommerce.common.logging.LoggingInterceptor;
import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebMvc
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {

	private final JwtAuthenticationInterceptor jwtAuthenticationInterceptor;
	private final AdminAuthorizationInterceptor adminAuthorizationInterceptor;

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(new LoggingInterceptor(new GlobalLogger()))
			.order(1);
		registry.addInterceptor(jwtAuthenticationInterceptor)
			.addPathPatterns("/api/cart/**", "/api/payment/**", "/api/order/**", "/api/images/products/upload")
			.excludePathPatterns("/api/payment/callback-approval/**")
			.excludePathPatterns("/api/payment/callback-cancel/**")
			.excludePathPatterns("/api/payment/callback-fail/**")
			.order(2);
		registry.addInterceptor(adminAuthorizationInterceptor)
			.addPathPatterns("/api/admin/**")
			.order(3);
	}

	@Override
	public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
		resolvers.add(new UserEmailArgumentResolver());
		resolvers.add(new SearchConditionRequestResolver());
	}

}