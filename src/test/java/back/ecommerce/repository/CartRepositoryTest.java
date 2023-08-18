package back.ecommerce.repository;

import static org.assertj.core.api.Assertions.*;

import java.util.List;

import javax.persistence.EntityManager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import back.ecommerce.domain.cart.Cart;
import back.ecommerce.domain.product.Category;
import back.ecommerce.domain.product.Product;
import back.ecommerce.dto.response.cart.CartProductDto;
import back.ecommerce.repository.cart.CartRepository;
import back.ecommerce.repository.product.ProductRepository;

@DataJpaTest
class CartRepositoryTest {
	@Autowired
	CartRepository cartRepository;
	@Autowired
	ProductRepository productRepository;

	@Autowired
	EntityManager entityManager;

	@BeforeEach
	void setUp() {
		Product product1 = new Product(null, "shirts", "brandA", 10000L, Category.TOP);
		Product product2 = new Product(null, "ring", "brandB", 15000L, Category.ACCESSORY);
		Product product3 = new Product(null, "jin", "brandC", 20000L, Category.PANTS);
		productRepository.save(product1);
		productRepository.save(product2);
		productRepository.save(product3);

		Cart cart1 = Cart.create("user@naver.com", product1, 2);
		Cart cart2 = Cart.create("user@naver.com", product2, 1);
		Cart cart3 = Cart.create("user@naver.com", product1, 1);

		cartRepository.save(cart1);
		cartRepository.save(cart2);
		cartRepository.save(cart3);

		entityManager.flush();
		entityManager.clear();
	}

	@Test
	@DisplayName("findByEmail 시 해당하는 이메일을 가지는 장바구니가 조회되어야한다.")
	void findByEmail() {
		//given
		//when
		List<Cart> actual = cartRepository.findByUserEmail("user@naver.com");
		Cart cart = actual.get(0);
		CartProductDto cartProductDto = cart.toDto();

		//then
		assertThat(actual).hasSize(3);
		assertThat(cartProductDto.getName()).isEqualTo("shirts");
		assertThat(cartProductDto.getBrandName()).isEqualTo("brandA");
		assertThat(cartProductDto.getPrice()).isEqualTo(20000L);
	}
}