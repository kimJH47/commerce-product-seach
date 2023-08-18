package back.ecommerce.dto.response.cart;

import back.ecommerce.domain.cart.Cart;
import back.ecommerce.domain.product.Category;
import back.ecommerce.domain.product.Product;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Getter
@Builder
public class CartProductDto {
	private final Long id;
	private final String name;
	private final String brandName;
	private final Long price;
	private final Category category;
	private final int quantity;

	public static CartProductDto create(Cart cart) {
		Product product = cart.getProduct();
		return CartProductDto.builder()
			.id(cart.getId())
			.name(cart.getProduct().getName())
			.category(product.getCategory())
			.brandName(product.getBrandName())
			.price(cart.getPrice())
			.quantity(cart.getQuantity())
			.build();
	}
}
