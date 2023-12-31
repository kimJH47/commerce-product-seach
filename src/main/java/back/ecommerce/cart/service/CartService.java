package back.ecommerce.cart.service;

import static back.ecommerce.exception.ErrorCode.*;
import static java.util.stream.Collectors.*;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import back.ecommerce.cart.entity.Cart;
import back.ecommerce.product.entity.Product;
import back.ecommerce.cart.dto.response.AddCartResponse;
import back.ecommerce.cart.dto.response.CartDeleteResponse;
import back.ecommerce.cart.dto.response.CartListResponse;
import back.ecommerce.cart.dto.response.CartProductDto;
import back.ecommerce.cart.dto.response.CartProducts;
import back.ecommerce.exception.CustomException;
import back.ecommerce.exception.ErrorCode;
import back.ecommerce.cart.repository.CartRepository;
import back.ecommerce.product.repository.ProductRepository;
import back.ecommerce.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CartService {

	private final CartRepository cartRepository;
	private final UserRepository userRepository;
	private final ProductRepository productRepository;

	@Transactional
	public AddCartResponse addProduct(String email, long productId, int quantity) {
		validateUserEmail(email);
		Product product = productRepository.findById(productId)
			.orElseThrow(() -> new CustomException(PRODUCT_NOT_FOUND));
		Cart userCart = Cart.create(email, product, quantity);
		return AddCartResponse.create(cartRepository.save(userCart));
	}

	@Transactional(readOnly = true)
	public CartListResponse findCartByUserEmail(String email) {
		validateUserEmail(email);
		CartProducts cartProducts = cartRepository.findByUserEmail(email).stream()
			.map(CartProductDto::create)
			.collect(collectingAndThen(toList(), CartProducts::create));
		return new CartListResponse(email, cartProducts);
	}

	private void validateUserEmail(String email) {
		if (!userRepository.existsByEmail(email)) {
			throw new CustomException(ErrorCode.USER_NOT_FOUND);
		}
	}

	public CartDeleteResponse deleteById(Long cartId, String email) {
		validateUserEmail(email);
		cartRepository.deleteById(cartId);
		return new CartDeleteResponse(email, cartId);
	}
}