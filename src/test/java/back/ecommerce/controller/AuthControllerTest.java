package back.ecommerce.controller;

import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.stream.Stream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import back.ecommerce.dto.request.LoginRequest;
import back.ecommerce.dto.response.token.TokenResponseDto;
import back.ecommerce.exception.PasswordNotMatchedException;
import back.ecommerce.exception.UserNotFoundException;
import back.ecommerce.service.AuthService;

@WebMvcTest(AuthController.class)
@Import(MockAuthProviderConfig.class)
class AuthControllerTest {

	@Autowired
	MockMvc mockMvc;
	ObjectMapper mapper = new ObjectMapper();
	@MockBean
	AuthService authService;

	@Test
	@DisplayName("/api/auth/token POST 요청 시 인증 성공 후 상태코드 200과 함깨 엑세스 토큰이 응답 되어야 한다.")
	void login() throws Exception {
		//given
		String accessToken = "dsdasdmdsamkdsal.sddsamkldmlsa.dsamlkdmsaml21salkdm";
		int expireTime = 10000;
		String bearer = "Bearer";
		TokenResponseDto actual = new TokenResponseDto(accessToken, expireTime, bearer);

		given(authService.createToken(anyString(), anyString()))
			.willReturn(actual);

		//expect
		mockMvc.perform(post("/api/auth/token")
				.contentType(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsString(new LoginRequest("dsldsalw42@email.com", "dsamkcmx#dsm"))))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.message").value("인증이 성공적으로 완료되었습니다."))
			.andExpect(jsonPath("$.entity.accessToken").value(accessToken))
			.andExpect(jsonPath("$.entity.expireTime").value(expireTime))
			.andExpect(jsonPath("$.entity.type").value(bearer));

		then(authService).should(times(1))
			.createToken(anyString(), anyString());
	}

	@ParameterizedTest
	@MethodSource("invalidLoginRequestProvider")
	@DisplayName("/api/auth/token POST 으로 유효하지않은 데이터가 요청으로 오면 응답코드 404 와 함께 실패이유가 응답 되어야한다.")
	void login_exception(LoginRequest request, String fieldName, String reason) throws Exception {
		//expect
		mockMvc.perform(post("/api/auth/token")
				.contentType(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsString(request)))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.message").value("잘못된 요청입니다."))
			.andExpect(jsonPath("$.reasons." + fieldName).value(reason));

	}

	@Test
	@DisplayName("/api/auth/token POST 로 유효하지않은 데이터가 여러개 일 때 응답코드 404 와 함께 실패이유가 전부 응답 되어야한다. ")
	void login_exception_all() throws Exception {
		//given
		LoginRequest request = new LoginRequest("  ", "  ");

		//expect
		mockMvc.perform(post("/api/auth/token")
				.contentType(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsString(request)))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.message").value("잘못된 요청입니다."))
			.andExpect(jsonPath("$.reasons.email").isNotEmpty())
			.andExpect(jsonPath("$.reasons.password").value("비밀번호는 필수적으로 필요합니다."));

	}

	public static Stream<Arguments> invalidLoginRequestProvider() {
		return Stream.of(
			Arguments.of(new LoginRequest("", "asdmlsd2412"), "email", "이메일은 필수적으로 필요합니다."),
			Arguments.of(new LoginRequest("123@naver.com", ""), "password", "비밀번호는 필수적으로 필요합니다.")
		);
	}

	@Test
	@DisplayName("/api/auth/token POST 로 존재하지 않은 이메일을 보내면 응답코드 404 와 함께 실패이유가 응답 되어야한다.")
	void login_userNotFoundException() throws Exception {
	    //given
		given(authService.createToken(anyString(), anyString()))
			.willThrow(new UserNotFoundException("해당하는 유저가 존재하지 않습니다."));

	    //expect
		mockMvc.perform(post("/api/auth/token")
				.contentType(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsString(new LoginRequest("dsldsalw42@email.com", "dsamkcmx#dsm"))))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.message").value("잘못된 요청입니다."))
			.andExpect(jsonPath("$.reasons.login").value("해당하는 유저가 존재하지 않습니다."));
	}

	@Test
	@DisplayName("/api/auth/token POST 로 일치하지않은 비밀번호를 보내면 응답코드 404 와 함께 실패이유가 응답 되어야한다.")
	void login_passwordNotMatchException() throws Exception {
		//given
		given(authService.createToken(anyString(), anyString()))
			.willThrow(new PasswordNotMatchedException("비밀번호가 일치하지 않습니다."));

		//expect
		mockMvc.perform(post("/api/auth/token")
				.contentType(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsString(new LoginRequest("dsldsalw42@email.com", "dsamkcmx#dsm"))))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.message").value("잘못된 요청입니다."))
			.andExpect(jsonPath("$.reasons.login").value("비밀번호가 일치하지 않습니다."));
	}

}