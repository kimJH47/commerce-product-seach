package back.ecommerce.config.web;

import java.util.List;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import back.ecommerce.api.resolver.SearchConditionRequestResolver;
import back.ecommerce.auth.interceptor.AdminAuthorizationInterceptor;
import back.ecommerce.auth.interceptor.JwtAuthenticationInterceptor;
import back.ecommerce.auth.resolver.UserEmailArgumentResolver;
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
		registry.addInterceptor(new LoggingInterceptor())
			.order(1);
		registry.addInterceptor(jwtAuthenticationInterceptor)
			.addPathPatterns("/api/cart/**", "/api/payment/**", "api/order/**")
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

	@Override
	public void addCorsMappings(final CorsRegistry registry) {
		registry.addMapping("/**")
			.allowedOrigins("http://localhost:9000")
			.allowedMethods("GET", "POST")
			.maxAge(3000);
	}
}