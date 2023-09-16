package back.ecommerce.auth.interceptor;

import static back.ecommerce.exception.ErrorCode.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import back.ecommerce.auth.token.TokenProvider;
import back.ecommerce.exception.AuthenticationException;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationInterceptor implements HandlerInterceptor {

	private static final String AUTHORIZATION_TYPE = "Bearer ";
	private static final String EMAIL_ATTRIBUTE = "email";

	private final TokenProvider tokenProvider;

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws
		Exception {
		String token = extractHeaderToToken(request.getHeader(HttpHeaders.AUTHORIZATION));
		String email = tokenProvider.extractClaim(token, EMAIL_ATTRIBUTE);
		request.setAttribute(EMAIL_ATTRIBUTE, email);
		return true;
	}

	private String extractHeaderToToken(String header) {
		if (!StringUtils.hasText(header)) {
			throw new AuthenticationException(AUTH_HEADER_IS_EMPTY);
		}
		if (!header.startsWith(AUTHORIZATION_TYPE)) {
			throw new AuthenticationException(AUTH_HEADER_INVALID);
		}
		return header.substring(AUTHORIZATION_TYPE.length());
	}
}
