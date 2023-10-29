package back.ecommerce.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import back.ecommerce.cart.entity.Cart;
import back.ecommerce.product.entity.Category;
import back.ecommerce.product.entity.Product;
import back.ecommerce.cart.dto.response.AddCartResponse;
import back.ecommerce.cart.dto.response.CartListResponse;
import back.ecommerce.exception.CustomException;
import back.ecommerce.exception.ErrorCode;
import back.ecommerce.cart.repository.CartRepository;
import back.ecommerce.product.repository.ProductRepository;
import back.ecommerce.user.repository.UserRepository;
import back.ecommerce.cart.service.CartService;

@ExtendWith(MockitoExtension.class)
class CartServiceTest {
	@Mock
	CartRepository cartRepository;
	@Mock
	UserRepository userRepository;
	@Mock
	ProductRepository productRepository;
	CartService cartService;

	@BeforeEach
	void setUp() {
		cartService = new CartService(cartRepository, userRepository, productRepository);
	}

	@Test
	@DisplayName("카트에 상품이 저장되어야한다.")
	void cart_addProduct() {
		//given
		String email = "ImUser@email.com";
		long productId = 1L;
		int quantity = 10;
		Product product = createProduct(2L, "티셔츠", "커버낫", 35000L, Category.TOP);

		given(userRepository.existsByEmail(anyString()))
			.willReturn(true);
		given(productRepository.findById(anyLong()))
			.willReturn(Optional.of(product));
		given(cartRepository.save(any(Cart.class)))
			.willReturn(new Cart(10L, product, email, quantity, 350000L));

		//expect
		AddCartResponse addCartResponse = cartService.addProduct(email, productId, quantity);

		assertThat(addCartResponse.getId()).isEqualTo(10);
		assertThat(addCartResponse.getPrice()).isEqualTo(350000L);
		assertThat(addCartResponse.getQuantity()).isEqualTo(quantity);

		then(userRepository).should(times(1)).existsByEmail(anyString());
		then(productRepository).should(times(1)).findById(anyLong());
		then(cartRepository).should(times(1)).save(any(Cart.class));
	}

	@Test
	@DisplayName("카트에 상품추가 시 사용자의 이메일이 존재하지 않으면 UserNotFoundException 이 발생한다.")
	void cart_adProduct_userNotFoundException() {
		//given
		String email = "ImUser@email.com";
		long productId = 1L;
		int quantity = 10;

		given(userRepository.existsByEmail(anyString()))
			.willReturn(false);

		//expect
		assertThatThrownBy(() -> cartService.addProduct(email, productId, quantity))
			.isInstanceOf(CustomException.class)
			.hasMessage("해당하는 유저가 존재하지 않습니다.");

		then(userRepository).should(times(1)).existsByEmail(anyString());
		then(productRepository).should(times(0)).findById(anyLong());
		then(cartRepository).should(times(0)).save(any(Cart.class));
	}

	@Test
	@DisplayName("카트에 상품 추가시 상품정보가 존재하지 않으면 ProductNotFoundException 이 발생한다.")
	void cart_addProduct_productNotFoundException() {
		//given
		String email = "ImUser@email.com";
		long productId = 1L;
		int quantity = 10;

		given(userRepository.existsByEmail(anyString()))
			.willReturn(true);
		given(productRepository.findById(anyLong()))
			.willThrow(new CustomException(ErrorCode.PRODUCT_NOT_FOUND));

		//expect
		assertThatThrownBy(() -> cartService.addProduct(email, productId, quantity))
			.isInstanceOf(CustomException.class)
			.hasMessage("해당하는 상품이 존재하지 않습니다.");

		then(userRepository).should(times(1)).existsByEmail(anyString());
		then(productRepository).should(times(1)).findById(anyLong());
		then(cartRepository).should(times(0)).save(any(Cart.class));
	}

	@Test
	@DisplayName("사용자 이메일에 해당하는 장바구니 정보들을 반환해야한다.")
	void cart_findByUserEmail() {
		//given
		List<Cart> carts = new ArrayList<>();
		String userEmail = "user@email.com";

		Product productA = createProduct(1, "productA", "brandA", 100000L, Category.TOP);
		Product productB = createProduct(2, "productB", "brandB", 30000L, Category.PANTS);
		Product productC = createProduct(3, "productC", "brandC", 50000L, Category.ACCESSORY);

		carts.add(new Cart(1L, productA, userEmail, 1, 100000L));
		carts.add(new Cart(2L, productB, userEmail, 2, 60000L));
		carts.add(new Cart(3L, productC, userEmail, 1, 50000L));

		given(userRepository.existsByEmail(anyString()))
			.willReturn(true);
		given(cartRepository.findByUserEmail(anyString()))
			.willReturn(carts);

		//when
		CartListResponse response = cartService.findCartByUserEmail(userEmail);

		//then
		assertThat(response.getEmail()).isEqualTo(userEmail);
		assertThat(response.getCartProducts().getCount()).isEqualTo(3);
		assertThat(response.getCartProducts().getTotalPrice()).isEqualTo(210000L);
		assertThat(response.getCartProducts().getValue()).hasSize(3)
			.extracting("id", "name", "brandName", "price", "category", "quantity")
			.containsExactlyInAnyOrder(
				tuple(1L, "productA", "brandA", 100000L, Category.TOP, 1),
				tuple(2L, "productB", "brandB", 60000L, Category.PANTS, 2),
				tuple(3L, "productC", "brandC", 50000L, Category.ACCESSORY, 1)
			);

		then(userRepository).should(times(1)).existsByEmail(anyString());
		then(cartRepository).should(times(1)).findByUserEmail(anyString());
	}

	@Test
	@DisplayName("사용자가 존재하지 않으면 UserNotFoundException 이 발생한다.")
	void cart_findByUserEmail_UserNotFoundException() {
		//given
		given(userRepository.existsByEmail(anyString()))
			.willReturn(false);

		//expect
		assertThatThrownBy(() -> cartService.findCartByUserEmail("none@email.com"))
			.isInstanceOf(CustomException.class)
			.hasMessage("해당하는 유저가 존재하지 않습니다.");
	}

	private Product createProduct(long id, String name, String brandName, long price, Category category) {
		return new Product(id, name, brandName, price, category);
	}

}