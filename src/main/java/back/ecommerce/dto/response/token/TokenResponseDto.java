package back.ecommerce.dto.response.token;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class TokenResponseDto {
	private String accessToken;
	private int expireTime;
	private String type;

	public TokenResponseDto create(String token, int expireTime, String type) {
		return new TokenResponseDto(token, expireTime, type);
	}
}
