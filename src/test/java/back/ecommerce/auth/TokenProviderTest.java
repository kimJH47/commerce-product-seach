package back.ecommerce.auth;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import back.ecommerce.auth.token.Token;
import back.ecommerce.auth.token.TokenProvider;
import back.ecommerce.exception.AuthenticationException;
import back.ecommerce.exception.CustomException;
import io.jsonwebtoken.Jwts;

@SpringBootTest(classes = TokenProvider.class)
class TokenProviderTest {

	@Autowired
	TokenProvider tokenProvider;

	@Value("${jwt.secretKey}")
	private String securityKey;

	@Test
	@DisplayName("JWT 토큰이 생성 되어야한다.")
	void provideToken() {
		//given
		String expected = "dmkl1s@gmail.com";
		int expireTime = 1000 * 60 * 60;
		//when
		Token token = tokenProvider.provide(expected);
		String actual = Jwts.parser()
			.setSigningKey(securityKey)
			.parseClaimsJws(token.getValue())
			.getBody()
			.get("email").toString();
		//then
		assertThat(actual).isEqualTo(expected);
	}

	@Test
	@DisplayName("토큰에 유효기간이 만료되면 TokenHasExpiredException 이 발생한다.")
	void validate_expireDate() {
		//given
		String expected = "dmkl1s@gmail.com";
		int expireTime = -10000;
		Token token = tokenProvider.provide(expected);

		//expect
		assertThatThrownBy(() -> tokenProvider.extractClaim(token.getValue(), "email"))
			.isInstanceOf(AuthenticationException.class)
			.hasMessage("토큰이 만료 되었습니다.");
	}

	@Test
	@DisplayName("토큰이 비어있으면 TokenIsEmpty 가 발생 한다")
	void validate_empty_token() {
		//expect
		assertThatThrownBy(() ->
			tokenProvider.extractClaim(null, "email"))
			.isInstanceOf(AuthenticationException.class)
			.hasMessage("토큰이 비어있습니다.");


	}

	@Test
	@DisplayName("토큰이 유효하지 않으면 TokenHasInvalidException 이 발생한다.")
	void validate_invalidToken() {
		//given
		String invalidToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9"
			+ ".eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ"
			+ ".SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c";

		//expect
		assertThatThrownBy(() -> tokenProvider.extractClaim(invalidToken,"email"))
			.isInstanceOf(CustomException.class)
			.hasMessage("토큰이 유효하지 않습니다.");
	}

	@Test
	@DisplayName("토큰 페이로드의 클레임이 정상적으로 파싱되어야한다.")
	void parse_payload() {
		//given
		String expected1 = "dmkl1s@gmail.com";
		String expected2 = "ontfasdmkl1s@gmail.co.kr";
		int expireTime = 1000 * 60 * 60;
		Token token1 = tokenProvider.provide(expected1);
		Token token2 = tokenProvider.provide(expected2);

		//when
		String actual1 = tokenProvider.extractClaim(token1.getValue(), "email");
		String actual2 = tokenProvider.extractClaim(token2.getValue(), "email");

		//then
		assertThat(actual1).isEqualTo(expected1);
		assertThat(actual2).isEqualTo(expected2);
	}

	@Test
	@DisplayName("토큰의 페이로드에 요청한 값이 존재하지 않으면 빈 문자열이 발생한다.")
	void parse_payload_tokenHasInvalidException() {
		//given
		String expected = "dmkl1s@gmail.com";
		int expireTime = 1000 * 60 * 60;
		Token token = tokenProvider.provide(expected);

		//expect
		String actual = tokenProvider.extractClaim(token.getValue(), "email@@");
		assertThat(actual).isEmpty();
	}
}