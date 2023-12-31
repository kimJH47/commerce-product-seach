package back.ecommerce.auth.service;

import static back.ecommerce.exception.ErrorCode.*;

import java.time.LocalDateTime;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import back.ecommerce.auth.token.Token;
import back.ecommerce.auth.token.TokenProvider;
import back.ecommerce.common.generator.UuidGenerator;
import back.ecommerce.user.entity.User;
import back.ecommerce.auth.dto.response.SignUpDto;
import back.ecommerce.auth.dto.response.TokenResponse;
import back.ecommerce.auth.dto.response.SignUpResponse;
import back.ecommerce.exception.AuthenticationException;
import back.ecommerce.exception.CustomException;
import back.ecommerce.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

	private static final int JWT_TOKEN_EXPIRED_TIME = 1000 * 60 * 60;

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final TokenProvider tokenProvider;
	private final UuidGenerator uuidGenerator;
	private final SignUpService signUpService;

	@Transactional(readOnly = true)
	public TokenResponse createToken(String email, String password) {
		User user = userRepository.findByEmail(email)
			.orElseThrow(() -> new CustomException(USER_NOT_FOUND));
		if (!passwordEncoder.matches(password, user.getPassword())) {
			throw new AuthenticationException(PASSWORD_NOT_MATCHED);
		}
		Token token = tokenProvider.create(email, JWT_TOKEN_EXPIRED_TIME);
		return TokenResponse.create(token.getValue(), token.getExpireTime(), token.getType());
	}

	public SignUpDto signUp(String email, String password) {
		String code = uuidGenerator.create();
		signUpService.saveUserSignUpInfo(code, email, password);
		return new SignUpDto(email, createVerifiedUrl(code));
	}

	private String createVerifiedUrl(String code) {
		return "https://kecommerce.shop/auth/verified/" + code;
	}

	public SignUpResponse verifyEmailCode(String code) {
		String email = signUpService.verifyCodeAndSaveUser(code);
		return new SignUpResponse(email, LocalDateTime.now());
	}
}
