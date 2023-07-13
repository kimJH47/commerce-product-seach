package back.ecommerce.auth.interceptor;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import back.ecommerce.auth.token.TokenProvider;
import back.ecommerce.exception.AuthHeaderInvalidException;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationInterceptorTest {
	@Mock
	HttpServletRequest request;
	@Mock
	HttpServletResponse response;
	@Mock
	Object handle;
	@Mock
	TokenProvider tokenProvider;
	JwtAuthenticationInterceptor jwtAuthenticationInterceptor;

	@BeforeEach
	void setUp() {
		jwtAuthenticationInterceptor = new JwtAuthenticationInterceptor(tokenProvider);
	}

	@Test
	@DisplayName("유효한 토큰이 헤더에 담겨올 시 성공적으로 통괴가 되어야한다.")
	void authentication() throws Exception {
		//given
		String header = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9"
			+ ".eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ"
			+ ".3UsoYUK0_vvq2jwN6eskqTAC2E9xStyN7iVwZ7d3rw4";

		given(request.getHeader(anyString()))
			.willReturn(header);

		//when
		boolean actual = jwtAuthenticationInterceptor.preHandle(request, response, handle);

		//then
		assertThat(actual).isTrue();
		then(request).should(times(1)).getHeader(anyString());
		then(tokenProvider).should(times(1)).validate(anyString());
	}

	@Test
	@DisplayName(" authorization Header 의 인증 타입이 Bearer 가 아닐 시 AuthHeaderInvalidException 가 발생한다.")
	void authentication_invalidType() {
		//given
		String header = "Basic eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9"
			+ ".eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ"
			+ ".3UsoYUK0_vvq2jwN6eskqTAC2E9xStyN7iVwZ7d3rw4";

		given(request.getHeader(anyString()))
			.willReturn(header);

		//expect
		assertThatThrownBy(() -> jwtAuthenticationInterceptor.preHandle(request, response, handle))
			.isInstanceOf(AuthHeaderInvalidException.class)
			.hasMessage("인증 헤더타입이 일치하지 않습니다.");
	}

	@Test
	@DisplayName("authorization Header 가 비어있을 시 AuthHeaderInvalidException 가 발생한다.")
	void authentication_emptyHeader() {
		//given
		given(request.getHeader(anyString()))
			.willReturn(" ");

		//expect
		assertThatThrownBy(() -> jwtAuthenticationInterceptor.preHandle(request, response, handle))
			.isInstanceOf(AuthHeaderInvalidException.class)
			.hasMessage("인증 헤더가 비어있습니다.");

	}
}