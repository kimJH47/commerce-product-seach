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
import org.springframework.security.crypto.password.PasswordEncoder;

import back.ecommerce.auth.Token;
import back.ecommerce.auth.TokenProvider;
import back.ecommerce.domain.User;
import back.ecommerce.dto.response.token.TokenResponseDto;
import back.ecommerce.exception.PasswordNotMatchedException;
import back.ecommerce.exception.UserNotFoundException;
import back.ecommerce.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

	AuthService authService;
	@Mock
	UserRepository userRepository;
	@Mock
	PasswordEncoder passwordEncoder;
	@Mock
	TokenProvider tokenProvider;

	@BeforeEach
	void setUp() {
		authService = new AuthService(userRepository, passwordEncoder, tokenProvider);
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
		given(tokenProvider.create(anyString())).willReturn(expected);

		//when
		TokenResponseDto actual = authService.createToken("dmdasdlm@email.com", "ddmlasMKL#sla@");

		//then
		assertThat(actual.getAccessToken()).isEqualTo(expected.getValue());
		assertThat(actual.getExpireTime()).isEqualTo(expected.getExpireTime());
		assertThat(actual.getType()).isEqualTo(expected.getType());

		then(userRepository).should(times(1)).findByEmail(anyString());
		then(passwordEncoder).should(times(1)).matches(anyString(), anyString());
		then(tokenProvider).should(times(1)).create(anyString());
	}

	@Test
	@DisplayName("토큰 생성시 일치하는 이메일이 없으면 UserNotFoundException 이 발생해야한다.")
	void create_userNotFoundException() throws Exception {
	    //given
		given(userRepository.findByEmail(anyString()))
			.willReturn(Optional.empty());

	    //expect
		assertThatThrownBy(() -> authService.createToken("edsjl23@email.com", "dmaslkd@#mfd"))
			.isInstanceOf(UserNotFoundException.class)
			.hasMessage("해당하는 유저가 존재하지 않습니다.");

		then(userRepository).should(times(1)).findByEmail(anyString());
		then(passwordEncoder).should(times(0)).matches(anyString(), anyString());
		then(tokenProvider).should(times(0)).create(anyString());

	}

	@Test
	@DisplayName("토큰 생성시 비밀번호가 일치하지 않으면 PasswordNotMatchedException 이 발생해야한다.")
	void create_passwordNotMatchedException() throws Exception {
	    //given
		User user = new User(10L, "dmdasdlm@email.com", "ddmlasMKL#sla@");

		given(userRepository.findByEmail(anyString()))
			.willReturn(Optional.of(user));
		given(passwordEncoder.matches(anyString(), anyString()))
			.willReturn(false);

		//expect
		assertThatThrownBy(() -> authService.createToken("edsjl23@email.com", "dmaslkd@#mfd"))
			.isInstanceOf(PasswordNotMatchedException.class)
			.hasMessage("비밀번호가 일치하지 않습니다.");

		then(userRepository).should(times(1)).findByEmail(anyString());
		then(passwordEncoder).should(times(1)).matches(anyString(), anyString());
		then(tokenProvider).should(times(0)).create(anyString());
	}
}