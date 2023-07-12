package back.ecommerce.service;

import org.springframework.stereotype.Service;

import back.ecommerce.dto.response.token.TokenResponseDto;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {
	public TokenResponseDto createToken(String email, String password) {
		return null;
	}
}
