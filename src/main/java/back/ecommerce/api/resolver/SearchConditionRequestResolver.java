package back.ecommerce.api.resolver;

import java.util.Map;

import org.jetbrains.annotations.NotNull;
import org.springframework.core.MethodParameter;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import back.ecommerce.product.dto.request.ProductSearchConditionRequest;
import back.ecommerce.product.entity.Category;
import jakarta.servlet.http.HttpServletRequest;

public class SearchConditionRequestResolver implements HandlerMethodArgumentResolver {

	private static final String URI_SPLIT_REGEX = "/";
	private static final String URL_CRITERIA = "categories";

	@Override
	public boolean supportsParameter(MethodParameter parameter) {
		return parameter.hasParameterAnnotation(ProductSearchRequestMapping.class);
	}

	@Override
	public ProductSearchConditionRequest resolveArgument(@NotNull MethodParameter parameter,
		ModelAndViewContainer mavContainer,
		@NotNull NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
		return ProductSearchConditionRequest.Companion.create(extractCategory(webRequest),
			extractParameterToMap(webRequest));
	}

	private Category extractCategory(NativeWebRequest webRequest) {
		HttpServletRequest request = (HttpServletRequest)webRequest.getNativeRequest();
		String requestURI = request.getRequestURI();
		return extractCategory(requestURI);
	}

	private Map<String, String> extractParameterToMap(NativeWebRequest webRequest) {
		Map<String, String[]> parameterMap = webRequest.getParameterMap();
		Map<String, String> attributes = CollectionUtils.newLinkedHashMap(parameterMap.size());
		parameterMap.forEach((key, values) -> {
			if (values.length > 0) {
				attributes.put(key, values[0].strip());
			}
		});
		return attributes;
	}

	private Category extractCategory(String url) {
		String[] split = url.split(URI_SPLIT_REGEX);
		int index = 0;
		for (int i = 0; i < split.length; i++) {
			if (split[i].equals(URL_CRITERIA)) {
				index = i + 1;
				break;
			}
		}
		return Category.from(split[index]);
	}
}
