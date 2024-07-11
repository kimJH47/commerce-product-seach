package back.ecommerce.cart.dto.response;

import back.ecommerce.cart.entity.Cart;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class AddCartResponse {
	private final Long id;
	private final int quantity;
	private final Long price;
	public static AddCartResponse create(Cart cart) {
		return new AddCartResponse(cart.getId(),cart.getQuantity(), cart.getPrice());
	}
}
