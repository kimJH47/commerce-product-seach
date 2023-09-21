package back.ecommerce.dto.request.amdin;

import back.ecommerce.domain.product.ApprovalStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AddProductRequest {
	private Long requestId;
	private ApprovalStatus approvalStatus;
	private String email;
}
