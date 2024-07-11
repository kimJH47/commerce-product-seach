package back.ecommerce.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.password.PasswordEncoder;

import back.ecommerce.auth.service.VerificationURLGenerator;
import back.ecommerce.auth.token.Token;
import back.ecommerce.auth.token.TokenProvider;
import back.ecommerce.common.generator.RandomUUIDGenerator;
import back.ecommerce.api.MockMvcTestConfig;
import back.ecommerce.user.entity.User;
import back.ecommerce.auth.dto.response.SignUpDto;
import back.ecommerce.auth.dto.response.TokenResponse;
import back.ecommerce.exception.AuthenticationException;
import back.ecommerce.exception.CustomException;
import back.ecommerce.exception.ErrorCode;
import back.ecommerce.user.repository.UserRepository;
import back.ecommerce.auth.service.AuthService;
import back.ecommerce.auth.service.SignUpService;

@ExtendWith(MockitoExtension.class)
@Import(MockMvcTestConfig.class)
class AuthServiceTest {

	AuthService authService;
	@Mock
	UserRepository userRepository;
	@Mock
	PasswordEncoder passwordEncoder;
	@Mock
	TokenProvider tokenProvider;
	@Mock
	RandomUUIDGenerator randomUUIDGenerator;
	@Mock
	SignUpService signUpService;
	@Mock
	VerificationURLGenerator verificationURLGenerator;

	@BeforeEach
	void setUp() {
		authService = new AuthService(userRepository, passwordEncoder, tokenProvider, randomUUIDGenerator,
			signUpService, verificationURLGenerator, 100000000);
	}

	@Test
	@DisplayName("토큰이 담긴 dto 가 생성 되어야한다.")
	void create() {
		//given
		User user = new User(10L, "dmdasdlm@email.com", "ddmlasMKL#sla@");
		Token expected = new Token("samdmasdmklsadmkl.sadklasd@Eas.sadfk@$", 1000, "Bearer");

		given(userRepository.findByEmail(anyString()))
			.willReturn(Optional.of(user));
		given(passwordEncoder.matches(anyString(), anyString()))
			.willReturn(true);
		given(tokenProvider.provide(anyString(), anyInt())).willReturn(expected);

		//when
		TokenResponse actual = authService.createToken("dmdasdlm@email.com", "ddmlasMKL#sla@");

		//then
		assertThat(actual.getAccessToken()).isEqualTo(expected.getValue());
		assertThat(actual.getExpireTime()).isEqualTo(expected.getExpireTime());
		assertThat(actual.getType()).isEqualTo(expected.getType());

		then(userRepository).should(times(1)).findByEmail(anyString());
		then(passwordEncoder).should(times(1)).matches(anyString(), anyString());
		then(tokenProvider).should(times(1)).provide(anyString(), anyInt());
	}

	@Test
	@DisplayName("토큰 생성시 일치하는 이메일이 없으면 UserNotFoundException 이 발생해야한다.")
	void create_userNotFoundException() {
		//given
		given(userRepository.findByEmail(anyString()))
			.willReturn(Optional.empty());

		//expect
		assertThatThrownBy(() -> authService.createToken("edsjl23@email.com", "dmaslkd@#mfd"))
			.isInstanceOf(CustomException.class)
			.hasMessage("해당하는 유저가 존재하지 않습니다.");

		then(userRepository).should(times(1)).findByEmail(anyString());
		then(passwordEncoder).should(times(0)).matches(anyString(), anyString());
		then(tokenProvider).should(times(0)).provide(anyString(), anyInt());

	}

	@Test
	@DisplayName("토큰 생성시 비밀번호가 일치하지 않으면 PasswordNotMatchedException 이 발생해야한다.")
	void create_passwordNotMatchedException() {
		//given
		String email = "dmdasdlm@email.com";
		User user = new User(10L, email, "ddmlasMKL#sla@");

		given(userRepository.findByEmail(anyString()))
			.willReturn(Optional.of(user));
		given(passwordEncoder.matches(anyString(), anyString()))
			.willReturn(false);

		//expect
		assertThatThrownBy(() -> authService.createToken("edsjl23@email.com", "dmaslkd@#mfd"))
			.isInstanceOf(AuthenticationException.class)
			.hasMessage("비밀번호가 일치하지 않습니다.");

		then(userRepository).should(times(1)).findByEmail(anyString());
		then(passwordEncoder).should(times(1)).matches(anyString(), anyString());
		then(tokenProvider).should(times(0)).provide(anyString(), anyInt());
	}

	@Test
	@DisplayName("sign up 정보를 받아서 sign dto 를 반환 해야한다.")
	void sign_up() {
		//given
		String email = "tray@gmail.com";
		String password = "saldkmLM@@!#KJ!@";

		given(randomUUIDGenerator.create())
			.willReturn("133812312");
		given(verificationURLGenerator.generateVerificationURL(anyString())).willReturn("verificationUrl");

		//when
		SignUpDto signUpDto = authService.signUp(email, password);

		//then
		assertThat(signUpDto.getEmail()).isEqualTo(email);

		then(randomUUIDGenerator).should(times(1)).create();
		then(signUpService).should(times(1)).saveUserSignUpInfo(anyString(), anyString(), anyString());
		then(verificationURLGenerator).should(times(1)).generateVerificationURL(anyString());
	}

	@Test
	@DisplayName("이메일이 이미 존재하면 ExistsUserEmailException 예외가 발생한다.")
	void sign_up_exception() {
		//given
		String email = "tray@gmail.com";
		String password = "saldkmLM@@!#KJ!@";

		given(randomUUIDGenerator.create())
			.willReturn("133812312");
		doThrow(new CustomException(ErrorCode.DUPLICATE_USER_EMAIL)).when(signUpService)
			.saveUserSignUpInfo(anyString(), anyString(), anyString());

		//expect
		assertThatThrownBy(() -> authService.signUp(email, password))
			.isInstanceOf(CustomException.class)
			.hasMessage("이미 가입된 이메일이 존재합니다.");

		then(randomUUIDGenerator).should(times(1)).create();
		then(signUpService).should(times(1)).saveUserSignUpInfo(anyString(), anyString(), anyString());

	}

}