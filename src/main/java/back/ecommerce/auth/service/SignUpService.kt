package back.ecommerce.auth.service;

import static back.ecommerce.exception.ErrorCode.*;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import back.ecommerce.user.entity.SignUpInfo;
import back.ecommerce.user.entity.User;
import back.ecommerce.exception.CustomException;
import back.ecommerce.user.repository.SignUpRepository;
import back.ecommerce.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SignUpService {

	private static final long EMAIL_TOKEN_EXPIRED_SECONDS = 60 * 15; // min

	private final UserRepository userRepository;
	private final SignUpRepository signUpRepository;

	@Transactional
	public void saveUserSignUpInfo(String code, String email, String password) {
		validateEmail(email);
		signUpRepository.save(SignUpInfo.create(code, email, password, EMAIL_TOKEN_EXPIRED_SECONDS));
	}

	@Transactional
	public String verifyCodeAndSaveUser(String code) {
		SignUpInfo signUpInfo = signUpRepository.findByVerifiedCode(code)
			.orElseThrow(() -> new CustomException(EMAIL_CODE_NOT_FOUND));
		validateExpired(signUpInfo);
		validateEmail(signUpInfo.getEmail());
		userRepository.save(User.create(signUpInfo.getEmail(), signUpInfo.getPassword()));
		return signUpInfo.getEmail();
	}

	private void validateEmail(String email) {
		if (userRepository.existsByEmail(email)) {
			throw new CustomException(DUPLICATE_USER_EMAIL);
		}
	}

	private void validateExpired(SignUpInfo signUpInfo) {
		if (signUpInfo.isExpired()) {
			throw new CustomException(TOKEN_HAS_EXPIRED);
		}
	}
}
