package back.ecommerce.auth.token;

import static io.jsonwebtoken.SignatureAlgorithm.*;

import java.util.Date;
import java.util.HashMap;
import java.util.function.Supplier;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import back.ecommerce.exception.TokenHasExpiredException;
import back.ecommerce.exception.TokenHasInvalidException;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Component
public class TokenProvider {

	private static final String TYPE = "Bearer";
	private static final SignatureAlgorithm SIGNATURE_ALGORITHM = HS256;

	@Value("${jwt.secretKey}")
	private String securityKey;

	public Token create(String email, int expireTime) {

		HashMap<String, Object> payload = new HashMap<>();
		payload.put("email", email);

		String token = Jwts.builder()
			.setHeaderParam("typ", "JWT")
			.setHeaderParam("alg", SIGNATURE_ALGORITHM)
			.setClaims(payload)
			.setExpiration(createExpireTime(expireTime))
			.signWith(HS256, securityKey)
			.compact();

		return new Token(token, expireTime, TYPE);
	}

	private Date createExpireTime(int expireTime) {
		Date expireDate = new Date();
		expireDate.setTime(expireDate.getTime() + expireTime);
		return expireDate;
	}

	public void validate(String token) {
		if (token.isBlank()) {
			throw new TokenHasInvalidException("토큰이 존재하지 않습니다.");
		}
		execute(() -> Jwts.parser().setSigningKey(securityKey)
			.parseClaimsJws(token));
	}

	public String parsePayload(String token, String claimName) {
		return execute(() -> Jwts.parser().setSigningKey(securityKey)
			.parseClaimsJws(token)
			.getBody()
			.get(claimName, String.class));
	}

	private <T> T execute(Supplier<T> supplier) {
		try {
			T data = supplier.get();
			if (ObjectUtils.isEmpty(data)) {
				throw new RuntimeException();
			}
			return data;
		} catch (ExpiredJwtException e) {
			throw new TokenHasExpiredException("토큰이 만료 되었습니다.");
		} catch (Exception e) {
			throw new TokenHasInvalidException("토큰이 유효하지 않습니다.");
		}
	}
}
