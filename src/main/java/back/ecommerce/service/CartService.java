package back.ecommerce.service;

import org.springframework.stereotype.Service;

import back.ecommerce.dto.response.cart.CartListResponse;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CartService {
	public CartListResponse addProduct(String email, long productId, int quantity) {
		return null;
	}
}
