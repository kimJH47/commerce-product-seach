package back.ecommerce.service.auth;

import java.time.LocalDateTime;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import back.ecommerce.auth.token.EmailCodeProvider;
import back.ecommerce.auth.token.Token;
import back.ecommerce.auth.token.TokenProvider;
import back.ecommerce.domain.user.User;
import back.ecommerce.dto.response.auth.TokenResponse;
import back.ecommerce.dto.response.user.SignUpResponse;
import back.ecommerce.exception.PasswordNotMatchedException;
import back.ecommerce.exception.UserNotFoundException;
import back.ecommerce.repository.user.UserRepository;
import back.ecommerce.service.auth.email.EmailSender;
import back.ecommerce.service.auth.email.SignUpEmailMessage;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

	private static final int JWT_TOKEN_EXPIRED_TIME = 1000 * 60 * 60;
	private static final long EMAIL_TOKEN_EXPIRED_TIME = 1000 * 60 * 60 * 3;

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final TokenProvider tokenProvider;
	private final EmailCodeProvider emailCodeProvider;
	private final EmailSender emailSender;
	private final SignUpService signUpService;

	@Transactional(readOnly = true)
	public TokenResponse createToken(String email, String password) {
		User user = userRepository.findByEmail(email)
			.orElseThrow(() -> new UserNotFoundException("해당하는 유저가 존재하지 않습니다."));
		if (!passwordEncoder.matches(password, user.getPassword())) {
			throw new PasswordNotMatchedException("비밀번호가 일치하지 않습니다.");
		}
		Token token = tokenProvider.create(email, JWT_TOKEN_EXPIRED_TIME);
		return TokenResponse.create(token.getValue(), token.getExpireTime(), token.getType());
	}

	public SignUpResponse signUp(String email, String password) {
		String code = emailCodeProvider.create();
		signUpService.cachingSignUpInfo(code, email, password, EMAIL_TOKEN_EXPIRED_TIME);
		SignUpEmailMessage message = new SignUpEmailMessage(email, code, EMAIL_TOKEN_EXPIRED_TIME);
		emailSender.send(message);
		return new SignUpResponse(email, LocalDateTime.now());
	}

	public SignUpResponse verifiedEmailCode(String code) {
		String email = signUpService.verifiedCodeAndSaveUser(code);
		return new SignUpResponse(email, LocalDateTime.now());
	}
}
