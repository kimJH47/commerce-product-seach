package back.ecommerce.auth.resolver;

import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import back.ecommerce.auth.annotaion.UserEmail;
import back.ecommerce.exception.TokenHasInvalidException;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class UserEmailArgumentResolver implements HandlerMethodArgumentResolver {

	private static final String TOKEN_ATTRIBUTE = "email";

	@Override
	public boolean supportsParameter(MethodParameter parameter) {
		return parameter.getParameterAnnotation(UserEmail.class) != null
			&& parameter.getParameterType().equals(String.class);
	}

	@Override
	public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
		NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
		String email = (String)webRequest.getAttribute(TOKEN_ATTRIBUTE, RequestAttributes.SCOPE_REQUEST);
		if (email == null || email.isEmpty()) {
			throw new TokenHasInvalidException("인증이 성공적으로 되지 않았습니다.");
		}
		return email;
	}
}
