package back.ecommerce.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.fasterxml.jackson.databind.ObjectMapper;

import back.ecommerce.domain.product.Category;
import back.ecommerce.dto.CartProductDto;
import back.ecommerce.dto.request.AddCartRequest;
import back.ecommerce.dto.response.cart.CartListResponse;
import back.ecommerce.service.CartService;

@WebMvcTest(value = CartController.class, excludeFilters = {
	@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = WebMvcConfigurer.class)}
)
@Import(MockAuthProviderConfig.class)
class CartControllerTest {

	@Autowired
	MockMvc mockMvc;
	@MockBean
	CartService cartService;
	ObjectMapper mapper = new ObjectMapper();

	@Test
	@DisplayName("/api/cart/add-product POST 요청을 보내면 응답코드 200과 함께 등록된 카트 리스트가 응답되어야한다.")
	void add_product() throws Exception {
		//given
		String email = "user@email.com";
		List<CartProductDto> cartProducts = new ArrayList<>();
		cartProducts.add(createDto(1L, "맨투맨", "커버낫", Category.TOP, 2, 100000L));
		cartProducts.add(createDto(2L, "블랙진", "모드나인", Category.TOP, 1, 500000L));
		cartProducts.add(createDto(3L, "코트", "커버낫", Category.ONEPIECE, 1, 1000000L));

		given(cartService.findCartByUserEmail(anyString()))
			.willReturn(new CartListResponse(email, cartProducts.size(), 1700000L, cartProducts));

		//expect
		mockMvc.perform(post("/api/cart/add-product")
				.contentType(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsString(new AddCartRequest(email, 3L, 1))))
			.andExpect(status().isOk());

		then(cartService).should(times(1)).addProduct(anyString(), anyLong(), anyInt());
		then(cartService).should(times(1)).findCartByUserEmail(anyString());
	}

	@ParameterizedTest
	@MethodSource("invalidAddProductRequestProvider")
	@DisplayName("/api/cart/add-product POST 로 유효하지 않은 데이터를 요청으로 보낼시 응답코드 404와 함께 실패이유가 응답되어야 한다.")
	void add_product_invalid_request(AddCartRequest request, String fieldName, String message) throws Exception {
		//expect
		mockMvc.perform(post("/api/cart/add-product")
				.contentType(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsString(request)))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.reasons." + fieldName).value(message))
			.andDo(print());
	}

	public static Stream<Arguments> invalidAddProductRequestProvider() {
		return Stream.of(
			Arguments.of(new AddCartRequest("user123@naver.com", 15L, -1), "quantity", "상품의 갯수는 최소 1개 이상이여야 합니다."),
			Arguments.of(new AddCartRequest("user123@naver.com", 15L, 0), "quantity", "상품의 갯수는 최소 1개 이상이여야 합니다."),
			Arguments.of(new AddCartRequest("  ", 12L, 1), "email", "이메일은 필수적으로 필요합니다."),
			Arguments.of(new AddCartRequest("user123@@na.ver.com", 15L, 10), "email", "옳바른 이메일 형식이 아닙니다."),
			Arguments.of(new AddCartRequest("user123@naver.com", null, 10), "productId", "상품의 아이디는 필수적으로 필요합니다.")
		);
	}

	@Test
	@DisplayName("/api/cart/add-product POST 로 여러개의 유효하지 않은 데이터를 요청으로 보낼시 응답코드 404와 함께 실패이유가 전부 응답되어야 한다.")
	void add_product_invalid_request_all() throws Exception {
		AddCartRequest request = new AddCartRequest("  ", null, -1);
		//expect
		mockMvc.perform(post("/api/cart/add-product")
				.contentType(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsString(request)))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.reasons.email").isNotEmpty())
			.andExpect(jsonPath("$.reasons.productId").isNotEmpty())
			.andExpect(jsonPath("$.reasons.quantity").isNotEmpty())
			.andDo(print());

	}

	private CartProductDto createDto(long id, String name, String brandName, Category category, int quantity,
		long price) {
		return new CartProductDto(id, name, brandName, price, category, quantity);
	}

}