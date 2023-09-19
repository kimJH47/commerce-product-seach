package back.ecommerce.config.web;

import java.util.List;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import back.ecommerce.auth.interceptor.JwtAuthenticationInterceptor;
import back.ecommerce.auth.resolver.UserEmailArgumentResolver;
import back.ecommerce.common.logging.LoggingInterceptor;
import back.ecommerce.controller.resolver.SearchConditionRequestResolver;
import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebMvc
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {

	private final JwtAuthenticationInterceptor interceptor;

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(interceptor)
			.addPathPatterns("/api/cart/**");
		registry.addInterceptor(new LoggingInterceptor())
			.order(1);
	}

	@Override
	public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
		resolvers.add(new UserEmailArgumentResolver());
		resolvers.add(new SearchConditionRequestResolver());
	}
}
