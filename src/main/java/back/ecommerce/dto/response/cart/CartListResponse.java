package back.ecommerce.dto.response.cart;

import java.util.List;

import back.ecommerce.dto.CartProductDto;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class CartListResponse {
	private final String email;
	private final int count;
	private final Long totalPrice;
	private final List<CartProductDto> CartProducts;
}
