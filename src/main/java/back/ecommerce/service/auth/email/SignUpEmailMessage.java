package back.ecommerce.service.auth.email;

import lombok.Getter;

@Getter
public class SignUpEmailMessage {
	private final String email;
	private final String code;
	private final Long expiredTime;

	public SignUpEmailMessage(String email, String code, Long expiredTime) {
		this.email = email;
		this.code = code;
		this.expiredTime = expiredTime;
	}
}
