package back.ecommerce.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import back.ecommerce.auth.token.Token;
import back.ecommerce.auth.token.TokenProvider;
import back.ecommerce.domain.User;
import back.ecommerce.dto.response.token.TokenResponseDto;
import back.ecommerce.exception.PasswordNotMatchedException;
import back.ecommerce.exception.UserNotFoundException;
import back.ecommerce.repository.UserRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {
	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;

	private final TokenProvider tokenProvider;

	public TokenResponseDto createToken(String email, String password) {
		User user = userRepository.findByEmail(email)
			.orElseThrow(() -> new UserNotFoundException("해당하는 유저가 존재하지 않습니다."));

		if (!passwordEncoder.matches(user.getPassword(), password)) {
			throw new PasswordNotMatchedException("비밀번호가 일치하지 않습니다.");
		}
		Token token = tokenProvider.create(email);
		return TokenResponseDto.create(token.getValue(), token.getExpireTime(), token.getType());
	}
}
