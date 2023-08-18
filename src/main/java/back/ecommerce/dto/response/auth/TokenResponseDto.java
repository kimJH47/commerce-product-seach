package back.ecommerce.dto.response.auth;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class TokenResponseDto {
	private final String accessToken;
	private final int expireTime;
	private final String type;

	public static TokenResponseDto create(String token, int expireTime, String type) {
		return new TokenResponseDto(token, expireTime, type);
	}
}
