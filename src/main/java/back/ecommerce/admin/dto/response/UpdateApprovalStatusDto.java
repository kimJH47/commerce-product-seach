package back.ecommerce.admin.dto.response;

import java.util.HashMap;
import java.util.Map;

import back.ecommerce.product.entity.ApprovalStatus;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class UpdateApprovalStatusDto {
	private final String email;
	private final Long requestId;
	private final ApprovalStatus approvalStatus;

	public Map<String, String> toMap() {
		HashMap<String, String> map = new HashMap<>();
		map.put("email", email);
		map.put("requestId", requestId.toString());
		map.put("approvalStatus", approvalStatus.toString());
		return map;
	}
}
