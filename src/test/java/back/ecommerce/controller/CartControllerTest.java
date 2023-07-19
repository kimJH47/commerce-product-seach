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
import back.ecommerce.exception.ProductNotFoundException;
import back.ecommerce.exception.UserNotFoundException;
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
	@DisplayName("/api/cart/add-product POST 로 유효하지 않은 데이터를 요청으로 보낼시 응답코드 400와 함께 실패이유가 응답되어야 한다.")
	void add_product_invalid_request(AddCartRequest request, String fieldName) throws Exception {
		//expect
		mockMvc.perform(post("/api/cart/add-product")
				.contentType(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsString(request)))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.reasons." + fieldName).isNotEmpty());
	}

	public static Stream<Arguments> invalidAddProductRequestProvider() {
		return Stream.of(
			Arguments.of(new AddCartRequest("user123@naver.com", 15L, -1), "quantity"),
			Arguments.of(new AddCartRequest("user123@naver.com", 15L, 0), "quantity"),
			Arguments.of(new AddCartRequest("  ", 12L, 1), "email"),
			Arguments.of(new AddCartRequest("user123@@na.ver.com", 15L, 10), "email"),
			Arguments.of(new AddCartRequest("user123@naver.com", null, 10), "productId")
		);
	}

	@Test
	@DisplayName("/api/cart/add-product POST 로 여러개의 유효하지 않은 데이터를 요청으로 보낼시 응답코드 400와 함께 실패이유가 전부 응답되어야 한다.")
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

	@Test
	@DisplayName("/api/cart/add-product POST 로 존재하지 않는 사용자 이메일을 요청으로 보내면 응답코드 400")
	void add_product_userNotFoundException() throws Exception {
		//given
		doThrow(new UserNotFoundException("해당하는 유저가 존재하지 않습니다."))
			.when(cartService).addProduct(anyString(), anyLong(), anyInt());

		//expect
		mockMvc.perform(post("/api/cart/add-product")
				.contentType(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsString(new AddCartRequest("email@email.com", 100L, 10))))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.reasons.login").value("해당하는 유저가 존재하지 않습니다."));
	}

	@Test
	@DisplayName("")
	void add_product_productNotFoundException() throws Exception {
		//given
		doThrow(new ProductNotFoundException("해당하는 상품이 존재하지 않습니다."))
			.when(cartService).addProduct(anyString(), anyLong(), anyInt());

		//expect
		mockMvc.perform(post("/api/cart/add-product")
				.contentType(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsString(new AddCartRequest("email@email.com", 100L, 10))))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.reasons.product").value("해당하는 상품이 존재하지 않습니다."));

	}

	private CartProductDto createDto(long id, String name, String brandName, Category category, int quantity,
		long price) {
		return new CartProductDto(id, name, brandName, price, category, quantity);
	}

}