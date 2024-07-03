package back.ecommerce.api.payment;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Getter
@NoArgsConstructor
public class OrderProductDto {
	@NotNull(message = "상품의 아이디는 필수적으로 필요합니다.")
	private Long productId;
	private String name;
	@Min(value = 1,message = "상품의 갯수는 최소 1개 이상이여야 합니다.")
	private Integer quantity;
	@Min(value = 1,message = "상품의 갯수는 최소 1개 이상이여야 합니다.")
	@NotNull(message = "가격은 필수적으로 필요 합니다.")
	private Long price;
}
