package back.ecommerce.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import back.ecommerce.domain.Cart;
import back.ecommerce.domain.User;
import back.ecommerce.domain.product.Category;
import back.ecommerce.domain.product.Product;
import back.ecommerce.exception.ProductNotFoundException;
import back.ecommerce.exception.UserNotFoundException;
import back.ecommerce.repository.CartRepository;
import back.ecommerce.repository.ProductRepository;
import back.ecommerce.repository.UserRepository;

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
		User user = new User(10L, email, "sadmklasdmkl2KMEMKL#");
		Product product = new Product(2L, "티셔츠", "커버낫", 35000L, Category.TOP);

		given(userRepository.findByEmail(anyString()))
			.willReturn(Optional.of(user));
		given(productRepository.findById(anyLong()))
			.willReturn(Optional.of(product));

		//expect
		assertThatCode(() ->
			cartService.addProduct(email, productId, quantity))
			.doesNotThrowAnyException();

		then(userRepository).should(times(1)).findByEmail(anyString());
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

		given(userRepository.findByEmail(anyString()))
			.willThrow(new UserNotFoundException("해당하는 유저가 존재하지 않습니다."));

		//expect
		assertThatThrownBy(() -> cartService.addProduct(email, productId, quantity))
			.isInstanceOf(UserNotFoundException.class);

		then(userRepository).should(times(1)).findByEmail(anyString());
		then(productRepository).should(times(0)).findById(anyLong());
		then(cartRepository).should(times(0)).save(any(Cart.class));
	}

	@Test
	@DisplayName("카트에 상품 추가시 상품정보가 존재하지 않으면 ProductNotFoundException 이 발생한다.")
	void cart_addProduct_productNotFoundException() throws Exception {
		//given
		String email = "ImUser@email.com";
		long productId = 1L;
		int quantity = 10;
		User user = new User(10L, email, "sadmklasdmkl2KMEMKL#");

		given(userRepository.findByEmail(anyString()))
			.willReturn(Optional.of(user));
		given(productRepository.findById(anyLong()))
			.willThrow(new ProductNotFoundException("해당하는 상품이 존재하지 않습니다."));

		//expect
		assertThatThrownBy(() -> cartService.addProduct(email, productId, quantity))
			.isInstanceOf(ProductNotFoundException.class);

		then(userRepository).should(times(1)).findByEmail(anyString());
		then(productRepository).should(times(1)).findById(anyLong());
		then(cartRepository).should(times(0)).save(any(Cart.class));

	}

}