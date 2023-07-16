package back.ecommerce.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import back.ecommerce.domain.Cart;
import back.ecommerce.domain.User;
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

		User user = userRepository.findByEmail(email)
			.orElseThrow(() -> new UserNotFoundException("해당하는 유저가 존재하지 않습니다."));
		Product product = productRepository.findById(productId)
			.orElseThrow(() -> new ProductNotFoundException("해당하는 상품이 존재하지 않습니다."));

		Cart userCart = Cart.create(user, product, quantity);
		cartRepository.save(userCart);
	}

	@Transactional(readOnly = true)
	public CartListResponse findCartByUserEmail(String email) {
		return null;
	}
}
