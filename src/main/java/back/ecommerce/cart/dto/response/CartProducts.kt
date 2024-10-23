package back.ecommerce.cart.dto.response;

import java.util.Collections;
import java.util.List;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor
@Getter
public class CartProducts {
	private int count;
	private Long totalPrice;
	private List<CartProductDto> value;

	public static CartProducts create(List<CartProductDto> list) {
		long totalPrice = Collections.unmodifiableList(list).stream()
			.mapToLong(CartProductDto::getPrice)
			.sum();
		return new CartProducts(list.size(), totalPrice, list);
	}
}
