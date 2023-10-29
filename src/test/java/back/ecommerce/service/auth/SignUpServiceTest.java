package back.ecommerce.service.auth;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import back.ecommerce.auth.service.SignUpService;
import back.ecommerce.user.entity.SignUpInfo;
import back.ecommerce.user.entity.User;
import back.ecommerce.exception.CustomException;
import back.ecommerce.user.repository.SignUpRepository;
import back.ecommerce.user.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class SignUpServiceTest {

	@Mock
	UserRepository userRepository;
	@Mock
	SignUpRepository signUpRepository;
	@InjectMocks
	SignUpService signUpService;

	@Test
	@DisplayName("유저가입정보가 저장되어야 한다.")
	void save() {
		//given
		String email = "sw11@naver.com";
		given(userRepository.existsByEmail(email))
			.willReturn(false);

		//when
		assertThatCode(() -> signUpService.saveUserSignUpInfo("csdjasjdk12l3-231mdklsa-312", email, "sdnkn2jk#!"))
			.doesNotThrowAnyException();

		//then
		then(userRepository).should(times(1)).existsByEmail(anyString());
		then(signUpRepository).should(times(1)).save(any(SignUpInfo.class));
	}

	@Test
	@DisplayName("가입된 이메일이 존재하면 예외가 발생한다.")
	void save_duplicated_email() {
		//given
		String email = "sw11@naver.com";
		given(userRepository.existsByEmail(email))
			.willReturn(true);

		//when
		assertThatThrownBy(() -> signUpService.saveUserSignUpInfo("csdjasjdk12l3-231mdklsa-312", email, "sdnkn2jk#!"))
			.isInstanceOf(CustomException.class)
			.hasMessage("이미 가입된 이메일이 존재합니다.");

		//then
		then(userRepository).should(times(1)).existsByEmail(anyString());
		then(signUpRepository).should(times(0)).save(any(SignUpInfo.class));
	}

	@Test
	@DisplayName("이메일 코드검증이 성공적으로 완료되면 유저가 저장되고 이메일이 반환되어야 한다.")
	void verifyCodeAndSaveUser() {
		//given
		String code = "code";
		String email = "email";
		String password = "pass";

		given(signUpRepository.findByVerifiedCode(anyString()))
			.willReturn(Optional.of(new SignUpInfo(100L, email, password, code, 60 * 15L, LocalDateTime.now())));

		//when
		String actual = signUpService.verifyCodeAndSaveUser(code);

		//then
		assertThat(actual).isEqualTo(email);
		then(signUpRepository).should(times(1)).findByVerifiedCode(anyString());
		then(userRepository).should(times(1)).save(any(User.class));
	}

	@Test
	@DisplayName("이메일 코드가 존재하지않으면 예외가 발생한다.")
	void email_code_not_found() {
		//given
		given(signUpRepository.findByVerifiedCode(anyString()))
			.willReturn(Optional.empty());

		//when
		assertThatThrownBy(() -> signUpService.verifyCodeAndSaveUser("code"))
			.isInstanceOf(CustomException.class)
			.hasMessage("이메일 코드가 존재하지 않습니다.");

		//then
		then(signUpRepository).should(times(1)).findByVerifiedCode(anyString());
		then(userRepository).should(times(0)).save(any(User.class));
	}

	@Test
	@DisplayName("이메일 코드가 완료되면 예외가 발생한다.")
	void expired_code()  {
	    //given
		String code = "code";
		String email = "email";
		String password = "pass";

		given(signUpRepository.findByVerifiedCode(anyString()))
			.willReturn(Optional.of(new SignUpInfo(100L, email, password, code, 60 * 15L, LocalDateTime.now()
				.minusSeconds(60 * 16L))));

		//when
		assertThatThrownBy(() -> signUpService.verifyCodeAndSaveUser("code"))
			.isInstanceOf(CustomException.class)
			.hasMessage("토큰이 만료 되었습니다.");

	    //then
		then(signUpRepository).should(times(1)).findByVerifiedCode(anyString());
		then(userRepository).should(times(0)).save(any(User.class));


	}
}