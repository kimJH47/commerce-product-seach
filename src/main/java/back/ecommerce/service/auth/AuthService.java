package back.ecommerce.service.auth;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import back.ecommerce.auth.token.Token;
import back.ecommerce.auth.token.TokenProvider;
import back.ecommerce.domain.user.User;
import back.ecommerce.dto.response.auth.TokenResponseDto;
import back.ecommerce.exception.PasswordNotMatchedException;
import back.ecommerce.exception.UserNotFoundException;
import back.ecommerce.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

	private static final int EXPIRED_TIME = 1000 * 60 * 60;

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;

	private final TokenProvider tokenProvider;

	@Transactional(readOnly = true)
	public TokenResponseDto createToken(String email, String password) {
		User user = userRepository.findByEmail(email)
			.orElseThrow(() -> new UserNotFoundException("해당하는 유저가 존재하지 않습니다."));
		if (!passwordEncoder.matches(password, user.getPassword())) {
			throw new PasswordNotMatchedException("비밀번호가 일치하지 않습니다.");
		}
		Token token = tokenProvider.create(email, EXPIRED_TIME);
		return TokenResponseDto.create(token.getValue(), token.getExpireTime(), token.getType());
	}
}
