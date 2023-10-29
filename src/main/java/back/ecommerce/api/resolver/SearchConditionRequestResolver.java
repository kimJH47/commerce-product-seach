package back.ecommerce.api.resolver;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.jetbrains.annotations.NotNull;
import org.springframework.core.MethodParameter;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import back.ecommerce.product.entity.Category;
import back.ecommerce.product.dto.request.ProductSearchConditionRequest;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SearchConditionRequestResolver implements HandlerMethodArgumentResolver {

	private static final int CATEGORY_INDEX = 3;
	private static final String URI_SPLIT_REGEX = "/";

	@Override
	public boolean supportsParameter(MethodParameter parameter) {
		return parameter.hasParameterAnnotation(ProductSearchRequestMapping.class);
	}

	@Override
	public ProductSearchConditionRequest resolveArgument(@NotNull MethodParameter parameter,
		ModelAndViewContainer mavContainer,
		@NotNull NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
		return ProductSearchConditionRequest.create(extractCategory(webRequest), extractParameterToMap(webRequest));
	}

	private Category extractCategory(NativeWebRequest webRequest) {
		HttpServletRequest request = (HttpServletRequest)webRequest.getNativeRequest();
		String requestURI = request.getRequestURI();
		return Category.from(requestURI.split(URI_SPLIT_REGEX)[CATEGORY_INDEX]);
	}

	private Map<String, String> extractParameterToMap(NativeWebRequest webRequest) {
		Map<String, String[]> parameterMap = webRequest.getParameterMap();
		Map<String, String> attributes = CollectionUtils.newLinkedHashMap(parameterMap.size());
		parameterMap.forEach((key, values) -> {
			if (values.length > 0) {
				attributes.put(key, values[0]);
			}
		});
		return attributes;
	}
}
