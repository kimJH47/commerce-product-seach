package back.ecommerce.auth;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import io.jsonwebtoken.Jwts;

@SpringBootTest(classes = TokenProvider.class)
class TokenProviderTest {

	@Autowired
	TokenProvider tokenProvider;

	@Value("${jwt.secretKey}")
	private String securityKey;


	@Test
	@DisplayName("JWT 토큰이 생성 되어야한다.")
	void createToken(){
		//given
		String expected = "dmkl1s@gmail.com";

		//when
		Token token = tokenProvider.create(expected);
		String actual = Jwts.parser()
			.setSigningKey(securityKey)
			.parseClaimsJws(token.getValue())
			.getBody()
			.get("email").toString();

		//then
		Assertions.assertThat(actual).isEqualTo(expected);
	}



}