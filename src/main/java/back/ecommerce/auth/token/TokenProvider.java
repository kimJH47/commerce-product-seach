package back.ecommerce.auth.token;

import static io.jsonwebtoken.SignatureAlgorithm.*;

import java.util.Date;
import java.util.HashMap;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import back.ecommerce.exception.TokenHasInvalidException;
import io.jsonwebtoken.Jwts;

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
			.signWith(HS256, securityKey)
			.compact();

		return new Token(token, EXPIRED_TIME, TYPE);
	}

	private Date getExpireTime() {
		Date expireTime = new Date();
		expireTime.setTime(expireTime.getTime() + EXPIRED_TIME);
		return expireTime;
	}

	public void validate(String token) {
		if (token.isEmpty()) {
			throw new TokenHasInvalidException("토큰이 존재하지 않습니다.");
		}
		try {
			Jwts.parser().setSigningKey(securityKey)
				.parseClaimsJws(token);
		} catch (Exception e) {
			throw new TokenHasInvalidException("토큰이 유효하지 않습니다.");
		}
	}
}
