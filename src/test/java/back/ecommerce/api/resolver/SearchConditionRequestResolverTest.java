package back.ecommerce.api.resolver;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.core.MethodParameter;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.ServletWebRequest;

import back.ecommerce.product.entity.Category;
import back.ecommerce.product.dto.request.ProductSearchConditionRequest;
import back.ecommerce.exception.CustomException;

class SearchConditionRequestResolverTest {

	SearchConditionRequestResolver searchConditionRequestResolver;
	MethodParameter methodParameter;
	NativeWebRequest request;

	@BeforeEach
	void setUp() {
		searchConditionRequestResolver = new SearchConditionRequestResolver();
		methodParameter = Mockito.mock(MethodParameter.class);
		request = Mockito.mock(ServletWebRequest.class);
	}

	@Test
	@DisplayName("파라미터에 ProductSearchRequestMapping 어노테이션이 존재하면 true 를 반환한다.")
	void supportsParameter() {
		//given
		given(methodParameter.hasParameterAnnotation(ProductSearchRequestMapping.class))
			.willReturn(true);

		//when
		boolean actual = searchConditionRequestResolver.supportsParameter(methodParameter);

		//then
		assertThat(actual).isTrue();
		then(methodParameter).should(times(1))
			.hasParameterAnnotation(ProductSearchRequestMapping.class);
	}

	@Test
	@DisplayName("ProductSearRequest 가 매핑되어야한다.")
	void resolveArgument() {
		//given
		HashMap<String, String[]> hashMap = new HashMap<>();
		hashMap.put("name", new String[] {"nameData  "});
		hashMap.put("brandName", new String[] {"BrandC"});
		hashMap.put("maxPrice", new String[] {"  10000  "});
		hashMap.put("minPrice", new String[] {null});
		hashMap.put("page", new String[] {"13  "});
		hashMap.put("sort", new String[] {"new" });

		HttpServletRequest servletRequest = Mockito.mock(HttpServletRequest.class);

		given(request.getNativeRequest()).willReturn(servletRequest);
		given(servletRequest.getRequestURI()).willReturn("/api/categories/sneakers/details");
		given(request.getParameterMap())
			.willReturn(hashMap);

		//when
		ProductSearchConditionRequest actual = searchConditionRequestResolver.resolveArgument(methodParameter, null,
			request, null);

		//then
		assert actual != null;
		assertThat(actual.getCategory()).isEqualTo(Category.SNEAKERS);
		assertThat(actual.getParameters().get("name")).isEqualTo("nameData");
		assertThat(actual.getParameters().get("brandName")).isEqualTo("BrandC");
		assertThat(actual.getParameters().get("maxPrice")).isEqualTo("10000");
		assertThat(actual.getParameters().get("minPrice")).isEqualTo(null);
		assertThat(actual.getParameters().get("page")).isEqualTo("13");
		assertThat(actual.getParameters().get("sort")).isEqualTo("new");

		then(request).should(times(1)).getNativeRequest();
		then(servletRequest).should(times(1)).getRequestURI();
		then(request).should(times(1)).getParameterMap();
	}

	@Test
	@DisplayName("ProductSearRequest 매핑시 유효하지 않는 카테고리면 InvalidCategoryNameException 이 발생한다.")
	void resolveArgument_InvalidCategory() {
		//given
		HttpServletRequest servletRequest = Mockito.mock(HttpServletRequest.class);
		given(request.getNativeRequest()).willReturn(servletRequest);
		given(servletRequest.getRequestURI()).willReturn("/api/categories/sneakeris/details");

		//expect
		assertThatThrownBy(() -> searchConditionRequestResolver.resolveArgument(methodParameter, null,
			request, null))
			.isInstanceOf(CustomException.class)
			.hasMessage("유효하지 않은 카테고리명 입니다.");

		then(request).should(times(1)).getNativeRequest();
		then(servletRequest).should(times(1)).getRequestURI();
	}
}