package back.ecommerce.integration;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import back.ecommerce.api.auth.AuthController;
import back.ecommerce.api.dto.Response;
import back.ecommerce.api.dto.SuccessResponse;
import back.ecommerce.auth.dto.request.LoginRequest;
import back.ecommerce.auth.dto.request.SignUpRequest;
import back.ecommerce.auth.dto.response.SignUpResponse;
import back.ecommerce.auth.dto.response.TokenResponse;
import back.ecommerce.auth.service.AuthService;
import back.ecommerce.exception.CustomException;
import back.ecommerce.user.entity.SignUpInfo;
import back.ecommerce.user.entity.User;
import back.ecommerce.user.repository.SignUpRepository;
import back.ecommerce.user.repository.UserRepository;

@SpringBootTest
@Import(TestAwsConfig.class)
@Transactional
@Disabled
public class AuthApiTest {

	AuthController authController;
	@Autowired
	TestEmailPublisher testEmailPublisher;
	@Autowired
	AuthService authService;
	@Autowired
	UserRepository userRepository;
	@Autowired
	SignUpRepository signUpRepository;
	@Autowired
	PasswordEncoder passwordEncoder;

	@BeforeEach
	void setUp() {
		authController = new AuthController(authService, testEmailPublisher);
	}

	@Test
	void sign_up() {
		//given
		SignUpRequest signUpRequest = new SignUpRequest("kmr2644@gmail.com", "password");

		//when
		ResponseEntity<Response> responseResponseEntity = authController.signUp(signUpRequest);

		//then
		SuccessResponse<SignUpResponse> body = (SuccessResponse<SignUpResponse>)responseResponseEntity.getBody();

		assertThat(responseResponseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(body.getMessage()).isEqualTo("회원가입 요청이 성공적으로 완료되었습니다.");
		assertThat(body.getEntity().getEmail()).isEqualTo("kmr2644@gmail.com");
	}

	@Test
	void sign_up_duplicate_user() throws Exception {
		//given
		userRepository.save(new User(1L, "kmr2644@gmail.com", "password"));
		SignUpRequest signUpRequest = new SignUpRequest("kmr2644@gmail.com", "password");

		//expect
		assertThatThrownBy(() -> authController.signUp(signUpRequest))
			.isInstanceOf(CustomException.class)
			.hasMessage("이미 가입된 이메일이 존재합니다.");
	}

	@Test
	void get_token() throws Exception {
		//given
		String password = "password";
		String encode = passwordEncoder.encode(password);
		userRepository.save(new User(null, "kmr2644@gmail.com", encode));
		LoginRequest loginRequest = new LoginRequest("kmr2644@gmail.com", "password");

		//when
		ResponseEntity<Response> responseResponseEntity = authController.login(loginRequest);

		//then
		assertThat(responseResponseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
		SuccessResponse<TokenResponse> body = (SuccessResponse<TokenResponse>)responseResponseEntity.getBody();
		assertThat(body.getMessage()).isEqualTo("인증이 성공적으로 완료되었습니다.");
		assertThat(body.getEntity().getType()).isEqualTo("Bearer");
		assertThat(body.getEntity().getExpireTime()).isEqualTo(3600000);
	}

	@Test
	void verifyEmailCode() {
		//given
		UUID uuid = UUID.randomUUID();
		LocalDateTime now = LocalDateTime.now();
		SignUpInfo password = signUpRepository.save(
			new SignUpInfo(null, "kmr2644@gmail.com", "password", uuid.toString(), 1000L, now));
		//when
		ResponseEntity<Response> responseResponseEntity = authController.verifyEmailCode(uuid.toString());

		//then

		User user = userRepository.findByEmail("kmr2644@gmail.com").get();

		SuccessResponse<SignUpResponse> body = (SuccessResponse<SignUpResponse>)responseResponseEntity.getBody();

		assertThat(responseResponseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(body.getMessage()).isEqualTo("이메일 인증이 성공적으로 완료되었습니다.");
		assertThat(body.getEntity().getEmail()).isEqualTo("kmr2644@gmail.com");
		assertThat(user.getPassword()).isEqualTo("password");
	}

	@Test
	void verifyEmailCode_expired_code() {
	    //given
		UUID uuid = UUID.randomUUID();
		LocalDateTime now = LocalDateTime.now();
		SignUpInfo password = signUpRepository.save(
			new SignUpInfo(null, "kmr2644@gmail.com", "password", uuid.toString(), -10L, now));

	    //expect
		assertThatThrownBy(() -> authController.verifyEmailCode(uuid.toString()))
			.isInstanceOf(CustomException.class)
			.hasMessage("토큰이 만료 되었습니다.");
	}

	@Test
	void save_user_duplicate_user() {
	    //given
		UUID uuid = UUID.randomUUID();
		LocalDateTime now = LocalDateTime.now();
		SignUpInfo password = signUpRepository.save(
			new SignUpInfo(null, "kmr2644@gmail.com", "password", uuid.toString(), 1000L, now));
		userRepository.save(new User(null, "kmr2644@gmail.com", "passw1"));

		//expect
		assertThatThrownBy(() -> authController.verifyEmailCode(uuid.toString()))
			.isInstanceOf(CustomException.class)
			.hasMessage("이미 가입된 이메일이 존재합니다.");
	}
}
