package back.ecommerce.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.ArrayList;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.fasterxml.jackson.databind.ObjectMapper;

import back.ecommerce.auth.annotaion.UserEmail;
import back.ecommerce.controller.cart.CartController;
import back.ecommerce.controller.common.GlobalExceptionHandler;
import back.ecommerce.domain.product.Category;
import back.ecommerce.dto.request.cart.AddCartRequest;
import back.ecommerce.dto.response.cart.AddCartResponse;
import back.ecommerce.dto.response.cart.CartListResponse;
import back.ecommerce.dto.response.cart.CartProductDto;
import back.ecommerce.dto.response.cart.CartProducts;
import back.ecommerce.exception.ProductNotFoundException;
import back.ecommerce.exception.UserNotFoundException;
import back.ecommerce.service.cart.CartService;

@WebMvcTest(value = CartController.class, excludeFilters = {
	@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = WebMvcConfigurer.class)}
)
@Import(MockAuthProviderConfig.class)
class CartControllerTest {

	MockMvc mockMvc;
	@MockBean
	CartService cartService;
	ObjectMapper mapper = new ObjectMapper();

	@BeforeEach
	void setUp() {
		mockMvc = MockMvcBuilders.standaloneSetup(new CartController(cartService))
			.setCustomArgumentResolvers(new MockUserEmailArgumentResolver())
			.setControllerAdvice(new GlobalExceptionHandler())
			.build();
	}

	@Test
	@DisplayName("/api/cart/add-product POST 요청을 보내면 응답코드 200과 함께 등록된 카트 리스트가 응답되어야한다.")
	void add_product() throws Exception {
		//given
		String email = "user@email.com";
		given(cartService.addProduct(anyString(), anyLong(), anyInt()))
			.willReturn(new AddCartResponse(10L,1,10000L));
		//expect
		mockMvc.perform(post("/api/cart/add-product")
				.contentType(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsString(new AddCartRequest(email, 3L, 1))))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.id").value(10))
			.andExpect(jsonPath("$.quantity").value(1))
			.andExpect(jsonPath("$.price").value(10000));

		then(cartService).should(times(1)).addProduct(anyString(), anyLong(), anyInt());
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
	@DisplayName("/api/cart/add-product POST 로 존재하지 않는 사용자 이메일을 요청으로 보내면 응답코드 400과 함께 실패이유가 응답되어야한다.")
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
	@DisplayName("/api/cart/add-product POST 로 존재하지 않는 상품 아이디를 요청으로 보내면 응답코드 400과 함께 실패이유가 응답되어야한다.")
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

	@Test
	@DisplayName("/api/cart GET 로 요청을 보내면 사용자의 장바구니를 응답코드 200과 함께 응답한다.")
	void find() throws Exception {
		//given
		ArrayList<CartProductDto> cartProductDtos = new ArrayList<>();
		cartProductDtos.add(createDto(10, "shirts", "coverNat", Category.TOP, 2, 10000L));
		cartProductDtos.add(createDto(23, "cap", "carHartt", Category.HEAD_WEAR, 1, 8000L));
		cartProductDtos.add(createDto(35, "ring", "carHartt", Category.ACCESSORY, 1, 150000L));
		given(cartService.findCartByUserEmail(anyString()))
			.willReturn(new CartListResponse("user@email.com", CartProducts.create(cartProductDtos)));
		//expect
		mockMvc.perform(get("/api/cart"))
			.andExpect(status().isOk())
			.andDo(print())
			.andExpect(jsonPath("$.message").value("장바구니가 성곡적으로 조회 되었습니다."))
			.andExpect(jsonPath("$.entity.email").value("user@email.com"))
			.andExpect(jsonPath("$.entity.cartProducts.count").value(3))
			.andExpect(jsonPath("$.entity.cartProducts.totalPrice").value(168000))

			.andExpect(jsonPath("$.entity.cartProducts.value[0].id").value(10))
			.andExpect(jsonPath("$.entity.cartProducts.value[0].name").value("shirts"))
			.andExpect(jsonPath("$.entity.cartProducts.value[0].brandName").value("coverNat"))
			.andExpect(jsonPath("$.entity.cartProducts.value[0].category").value(Category.TOP.toString()))
			.andExpect(jsonPath("$.entity.cartProducts.value[0].quantity").value(2))
			.andExpect(jsonPath("$.entity.cartProducts.value[0].price").value(10000L))

			.andExpect(jsonPath("$.entity.cartProducts.value[1].id").value(23))
			.andExpect(jsonPath("$.entity.cartProducts.value[1].name").value("cap"))
			.andExpect(jsonPath("$.entity.cartProducts.value[1].brandName").value("carHartt"))
			.andExpect(jsonPath("$.entity.cartProducts.value[1].category").value(Category.HEAD_WEAR.toString()))
			.andExpect(jsonPath("$.entity.cartProducts.value[1].quantity").value(1))
			.andExpect(jsonPath("$.entity.cartProducts.value[1].price").value(8000L))

			.andExpect(jsonPath("$.entity.cartProducts.value[2].id").value(35))
			.andExpect(jsonPath("$.entity.cartProducts.value[2].name").value("ring"))
			.andExpect(jsonPath("$.entity.cartProducts.value[2].brandName").value("carHartt"))
			.andExpect(jsonPath("$.entity.cartProducts.value[2].category").value(Category.ACCESSORY.toString()))
			.andExpect(jsonPath("$.entity.cartProducts.value[2].quantity").value(1))
			.andExpect(jsonPath("$.entity.cartProducts.value[2].price").value(150000L));
	}

	@Test
	@DisplayName("/api/cart?email={email} GET 으로 존재하지 않은 사용자의 이메일을 보내면 응답코드 400 과 함께 실패이유가 응답되어야한다.")
	void find_userNotFoundException() throws Exception {
	    //given
		given(cartService.findCartByUserEmail(anyString()))
			.willThrow(new UserNotFoundException("해당하는 유저가 존재하지 않습니다."));

		//expect
		mockMvc.perform(get("/api/cart")
				.param("email", "user@email.com"))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.message").value("잘못된 요청입니다."))
			.andExpect(jsonPath("$.reasons.login").value("해당하는 유저가 존재하지 않습니다."));
	}

	private CartProductDto createDto(long id, String name, String brandName, Category category, int quantity,
		long price) {
		return new CartProductDto(id, name, brandName, price, category, quantity);
	}

	static class MockUserEmailArgumentResolver implements HandlerMethodArgumentResolver {

		@Override
		public boolean supportsParameter(MethodParameter parameter) {
			return parameter.getParameterAnnotation(UserEmail.class) != null
				&& parameter.getParameterType().equals(String.class);
		}

		@Override
		public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
			NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
			return "user@email.com";
		}
	}
}