package back.ecommerce.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import back.ecommerce.dto.response.cart.CartListResponse;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CartService {

	@Transactional
	public void addProduct(String email, long productId, int quantity) {
	}

	@Transactional(readOnly = true)
	public CartListResponse findCartByUserEmail(String email) {
		return null;
	}
}
