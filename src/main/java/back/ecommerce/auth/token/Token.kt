package back.ecommerce.auth.token;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Token {
	private final String value;
	private final int expireTime;
	private final String type;
}
