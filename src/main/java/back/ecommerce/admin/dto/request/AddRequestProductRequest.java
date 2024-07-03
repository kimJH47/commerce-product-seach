package back.ecommerce.admin.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class AddRequestProductRequest {

	@NotBlank(message = "이메일은 필수적으로 필요합니다.")
	@Email(message = "옳바른 이메일 형식이 아닙니다.")
	private String email;
	@NotBlank(message = "상품의 이름은 필수적으로 필요 합니다.")
	private String name;
	@NotBlank(message = "브랜드의 이름은 필수적으로 필요 합니다.")
	private String brandName;
	@NotNull(message = "가격정보는 필수적으로 필요 합니다.")
	@Min(value = 1, message = "상품의 가격은 최소 1 이상 이여야 합니다.")
	private Long price;
	@NotBlank(message = "상품의 카테고리는 필수적으로 필요 합니다.")
	private String category;

}
