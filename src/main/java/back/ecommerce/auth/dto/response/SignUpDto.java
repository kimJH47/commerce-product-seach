package back.ecommerce.auth.dto.response;

import java.util.HashMap;
import java.util.Map;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class SignUpDto {
	private final String email;
	private final String code;

	public Map<String, String> toMap() {
		HashMap<String, String> map = new HashMap<>();
		map.put("type", "email-code-smtp");
		map.put("email", email);
		map.put("code", code);
		return map;
	}
}
