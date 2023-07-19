package back.ecommerce.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import back.ecommerce.domain.Cart;
import back.ecommerce.domain.product.Product;
import back.ecommerce.dto.response.cart.CartListResponse;
import back.ecommerce.exception.ProductNotFoundException;
import back.ecommerce.exception.UserNotFoundException;
import back.ecommerce.repository.CartRepository;
import back.ecommerce.repository.ProductRepository;
import back.ecommerce.repository.UserRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CartService {

	private final CartRepository cartRepository;
	private final UserRepository userRepository;
	private final ProductRepository productRepository;

	@Transactional
	public void addProduct(String email, long productId, int quantity) {
		validateUserEmail(email);
		Product product = productRepository.findById(productId)
			.orElseThrow(() -> new ProductNotFoundException("해당하는 상품이 존재하지 않습니다."));

		Cart userCart = Cart.create(email, product, quantity);
		cartRepository.save(userCart);
	}

	private void validateUserEmail(String email) {
		if (!userRepository.existsByEmail(email)) {
			throw new UserNotFoundException("해당하는 유저가 존재하지 않습니다.");
		}
	}

	@Transactional(readOnly = true)
	public CartListResponse findCartByUserEmail(String email) {
		return null;
	}
}
