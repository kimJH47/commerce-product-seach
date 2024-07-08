package back.ecommerce.auth.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class TokenResponse {
	private final String accessToken;
	private final int expireTime;
	private final String type;

	public static TokenResponse create(String token, int expireTime, String type) {
		return new TokenResponse(token, expireTime, type);
	}
}
