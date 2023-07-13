package back.ecommerce.auth;

import java.util.Date;
import java.util.HashMap;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Component
public class TokenProvider {

	private static final int EXPIRED_TIME = 1000 * 60 * 60;
	private static final String TYPE = "Bearer";
	@Value("${jwt.secretKey}")
	private String securityKey;

	public Token create(String email) {

		HashMap<String, Object> payload = new HashMap<>();
		payload.put("email", email);

		String token = Jwts.builder()
			.setHeaderParam("typ", "JWT")
			.setHeaderParam("alg", "HS256")
			.setClaims(payload)
			.setExpiration(getExpireTime())
			.signWith(SignatureAlgorithm.HS256, securityKey)
			.compact();

		return new Token(token, EXPIRED_TIME, TYPE);
	}

	private Date getExpireTime() {
		Date expireTime = new Date();
		expireTime.setTime(expireTime.getTime() + EXPIRED_TIME);
		return expireTime;
	}
}
