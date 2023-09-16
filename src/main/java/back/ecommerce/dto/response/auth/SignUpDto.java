package back.ecommerce.dto.response.auth;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class SignUpDto {
	private final String email;
	private final String code;
}
