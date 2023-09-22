package back.ecommerce.controller;

import static back.ecommerce.exception.ErrorCode.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
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

import back.ecommerce.controller.auth.AuthController;
import back.ecommerce.dto.request.user.LoginRequest;
import back.ecommerce.dto.request.user.SignUpRequest;
import back.ecommerce.dto.response.auth.SignUpDto;
import back.ecommerce.dto.response.auth.TokenResponse;
import back.ecommerce.dto.response.user.SignUpResponse;
import back.ecommerce.exception.CustomException;
import back.ecommerce.publisher.aws.EmailSQSEventPublisher;
import back.ecommerce.service.auth.AuthService;

@WebMvcTest(AuthController.class)
@Import(MockMvcTestConfig.class)
class AuthControllerTest {

	@Autowired
	MockMvc mockMvc;
	ObjectMapper mapper = new ObjectMapper();
	@MockBean
	AuthService authService;
	@MockBean
	EmailSQSEventPublisher emailSQSEventPublisher;

	@Test
	@DisplayName("/api/auth/token POST 요청 시 인증 성공 후 상태코드 200과 함깨 엑세스 토큰이 응답 되어야 한다.")
	void login() throws Exception {
		//given
		String accessToken = "dsdasdmdsamkdsal.sddsamkldmlsa.dsamlkdmsaml21salkdm";
		int expireTime = 10000;
		String bearer = "Bearer";
		TokenResponse actual = new TokenResponse(accessToken, expireTime, bearer);

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
	@DisplayName("/api/auth/token POST 으로 유효하지않은 데이터가 요청으로 오면 응답코드 400 와 함께 실패이유가 응답 되어야한다.")
	void login_exception(LoginRequest request, String reason) throws Exception {
		//expect
		mockMvc.perform(post("/api/auth/token")
				.contentType(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsString(request)))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.message").value("잘못된 요청입니다."))
			.andExpect(jsonPath("$.reasons." + reason).isNotEmpty());

		then(authService).should(times(0)).createToken(anyString(), anyString());
	}

	public static Stream<Arguments> invalidLoginRequestProvider() {
		return Stream.of(
			Arguments.of(new LoginRequest(" ", "asdmlsd2412"), "email"),
			Arguments.of(new LoginRequest("email@@com.co", "asdmlsd2412"), "email"),
			Arguments.of(new LoginRequest("123@naver.com", ""), "password")
		);
	}

	@Test
	@DisplayName("/api/auth/token POST 로 유효하지않은 데이터가 여러개 일 때 응답코드 400 와 함께 실패이유가 전부 응답 되어야한다. ")
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

		then(authService).should(times(0)).createToken(anyString(), anyString());

	}

	@Test
	@DisplayName("/api/auth/token POST 로 존재하지 않은 이메일을 보내면 응답코드 400 와 함께 실패이유가 응답 되어야한다.")
	void login_userNotFoundException() throws Exception {
		//given
		given(authService.createToken(anyString(), anyString()))
			.willThrow(new CustomException(USER_NOT_FOUND));

		//expect
		mockMvc.perform(post("/api/auth/token")
				.contentType(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsString(new LoginRequest("dsldsalw42@email.com", "dsamkcmx#dsm"))))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.message").value("잘못된 요청입니다."))
			.andExpect(jsonPath("$.reasons.user").value("해당하는 유저가 존재하지 않습니다."));

		then(authService).should(times(1)).createToken(anyString(), anyString());
	}

	@Test
	@DisplayName("/api/auth/token POST 로 일치하지않은 비밀번호를 보내면 응답코드 400 와 함께 실패이유가 응답 되어야한다.")
	void login_passwordNotMatchException() throws Exception {
		//given
		given(authService.createToken(anyString(), anyString()))
			.willThrow(new CustomException(PASSWORD_NOT_MATCHED));

		//expect
		mockMvc.perform(post("/api/auth/token")
				.contentType(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsString(new LoginRequest("dsldsalw42@email.com", "dsamkcmx#dsm"))))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.message").value("잘못된 요청입니다."))
			.andExpect(jsonPath("$.reasons.password").value("비밀번호가 일치하지 않습니다."));

		then(authService).should(times(1)).createToken(anyString(), anyString());
	}

	@Test
	@DisplayName("/api/auth/sign-up POST 로 회원정보를 보내면 회원가입 대기상태가 성공적으로 완료 되어야 한다.")
	void sign_up() throws Exception {
		//given
		String email = "km@gmail.com";
		String password = "asd,lsaL:LE<@Q:#@!";
		String code = "11231212412";
		LocalDateTime now = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);

		given(authService.signUp(email, password))
			.willReturn(new SignUpDto(email, code));

		//expect
		mockMvc.perform(post("/api/auth/sign-up")
				.contentType(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsString(new SignUpRequest(email, password))))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.entity.email").value(email))
			.andExpect(jsonPath("$.entity.requestTime").value(now.toString()));

		then(authService).should(times(1)).signUp(anyString(), anyString());
		then(emailSQSEventPublisher).should(times(1)).pub(anyMap());
	}

	@ParameterizedTest
	@MethodSource("invalidSignRequestProvider")
	@DisplayName("/api/auth/sign-up POST 로 유효하지 않은 데이터를 보내면 응답코드 400 과 함께 실패 이유가 응답 되어야 한다.")
	void sign_up_invalid_email(SignUpRequest request, String field) throws Exception {
		//expect
		mockMvc.perform(post("/api/auth/sign-up")
				.contentType(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsString(request)))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.message").value("잘못된 요청입니다."))
			.andExpect(jsonPath("$.reasons." + field).isNotEmpty());

		then(authService).should(times(0)).signUp(anyString(), anyString());
		then(emailSQSEventPublisher).should(times(0)).pub(anyMap());
	}

	public static Stream<Arguments> invalidSignRequestProvider() {
		return Stream.of(
			Arguments.of(new SignUpRequest(" ", "asdmlsd2412"), "email"),
			Arguments.of(new SignUpRequest("email@@com.co", "asdmlsd2412"), "email"),
			Arguments.of(new SignUpRequest("123@naver.com", " "), "password")
		);
	}

	@Test
	@DisplayName("/api/auth/verified/{code} GET 요청을 보내면 이메일 유효성검증이 완료되어야 한다.")
	void verified() throws Exception {
		//given
		String code = "213214215";
		String email = "tra@gmail.com";
		LocalDateTime now = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);

		given(authService.verifyEmailCode(anyString()))
			.willReturn(new SignUpResponse(email, now));
		//expect
		mockMvc.perform(get("/api/auth/verified/" + code))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.entity.email").value(email))
			.andExpect(jsonPath("$.entity.requestTime").value(now.toString()));

		then(authService).should(times(1)).verifyEmailCode(anyString());
	}

	@Test
	@DisplayName("/api/auth/sign-up POST 요청으로 이미 존재하지 하는 이메일을 보내면 응답코드 400과 함께 실패이유를 응답해야한다.")
	void sign_up_existsUserEmail() throws Exception {
		//given
		String email = "km@gmail.com";
		String password = "asd,lsaL:LE<@Q:#@!";
		given(authService.signUp(anyString(), anyString()))
			.willThrow(new CustomException(DUPLICATE_USER_EMAIL));

		//expect
		mockMvc.perform(post("/api/auth/sign-up")
				.contentType(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsString(new SignUpRequest(email, password))))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.message").value("잘못된 요청입니다."))
			.andExpect(jsonPath("$.reasons.email").value("이미 가입된 이메일이 존재합니다."));

		then(authService).should(times(1)).signUp(anyString(), anyString());
		then(emailSQSEventPublisher).should(times(0)).pub(anyMap());
	}

	@Test
	@DisplayName("/api/auth/verified/{code} GET 으로 이메일 인증코드를 보낼 때 이미 존재하는 이메일이있으면 응답코드 400과 함께 실패이유를 응답해야한다.")
	void verified_existsUserEmail() throws Exception {
		//given
		given(authService.verifyEmailCode(anyString()))
			.willThrow(new CustomException(DUPLICATE_USER_EMAIL));

		//expect
		mockMvc.perform(get("/api/auth/verified/123213212"))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.message").value("잘못된 요청입니다."))
			.andExpect(jsonPath("$.reasons.email").value("이미 가입된 이메일이 존재합니다."));

		then(authService).should(times(1)).verifyEmailCode(anyString());
	}

	//verified 존재하는 이메일 검증, 코드존재하지않는 검증
	@Test
	@DisplayName("/api/auth/verified/{code} GET 으로 이메일 인증코드를 보낼 때 코드가 만료되었거나 존재하지 않으면 응답코드 400과 함께 실패이유를 응답해야한다.")
	void verified_codeNotFound() throws Exception {
		//given
		given(authService.verifyEmailCode(anyString()))
			.willThrow(new CustomException(EMAIL_CODE_NOT_FOUND));

		//expect
		mockMvc.perform(get("/api/auth/verified/123213212"))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.message").value("잘못된 요청입니다."))
			.andExpect(jsonPath("$.reasons.emailCode").value("이메일 코드가 존재하지 않습니다."));

		then(authService).should(times(1)).verifyEmailCode(anyString());
	}

}