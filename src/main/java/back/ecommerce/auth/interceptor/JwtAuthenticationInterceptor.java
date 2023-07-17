package back.ecommerce.auth.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import back.ecommerce.auth.token.TokenProvider;
import back.ecommerce.exception.AuthHeaderInvalidException;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationInterceptor implements HandlerInterceptor {

	private static final String AUTHORIZATION_TYPE = "Bearer ";

	private final TokenProvider tokenProvider;

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws
		Exception {
		String header = request.getHeader(HttpHeaders.AUTHORIZATION);
		String token = parseHeaderToToken(header);
		tokenProvider.validate(token);
		return HandlerInterceptor.super.preHandle(request, response, handler);
	}

	private String parseHeaderToToken(String header) {
		if (header == null || header.isBlank()) {
			throw new AuthHeaderInvalidException("인증 헤더가 비어있습니다.");
		}
		if (!header.startsWith(AUTHORIZATION_TYPE)) {
			throw new AuthHeaderInvalidException("인증 헤더타입이 일치하지 않습니다.");
		}
		return header.substring(AUTHORIZATION_TYPE.length());
	}
}
