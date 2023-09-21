package back.ecommerce.dto.request.amdin;

import java.util.HashMap;
import java.util.Map;

import back.ecommerce.domain.product.ApprovalStatus;
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
		map.put("type", "update-approval");
		map.put("email", email);
		map.put("requestId", requestId.toString());
		map.put("approvalStatus", approvalStatus.toString());
		return map;
	}
}
