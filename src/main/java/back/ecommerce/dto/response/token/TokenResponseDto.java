package back.ecommerce.dto.response.token;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class TokenResponseDto {
	private String accessToken;
	private String expireTime;
	private String type;

	public TokenResponseDto create(String token, String expireTime, String type) {
		return new TokenResponseDto(token, expireTime, type);
	}
}
