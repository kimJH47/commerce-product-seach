package back.ecommerce.api.auth.interceptor;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import back.ecommerce.admin.repository.AdminRepository;
import back.ecommerce.exception.AuthenticationException;
import back.ecommerce.exception.ErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class AdminAuthorizationInterceptor implements HandlerInterceptor {

	private static final String EMAIL_ATTRIBUTE = "email";

	private final AdminRepository adminRepository;
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws
		Exception {
		String email = (String)request.getAttribute(EMAIL_ATTRIBUTE);
		adminRepository.findByEmail(email)
			.orElseThrow(() -> new AuthenticationException(ErrorCode.ADMIN_NOT_FOUND));
		return HandlerInterceptor.super.preHandle(request, response, handler);
	}
}
