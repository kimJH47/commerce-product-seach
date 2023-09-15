package back.ecommerce.service.auth;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import back.ecommerce.exception.ExistsUserEmailException;
import back.ecommerce.repository.user.UserRepository;
import back.ecommerce.service.auth.email.SignUpEmailMessage;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SignUpService {

	private final UserRepository userRepository;
	private final RedisTemplate<String, String> redisTemplate;

	private final ObjectMapper objectMapper;

	public void cachingSignUpMessage(SignUpEmailMessage message) {
		validateEmail(message.getEmail());
		saveSignUpInfo(message);
	}

	@Transactional(readOnly = true)
	public void validateEmail(String email) {
		if (userRepository.existsByEmail(email)) {
			throw new ExistsUserEmailException("이미 가입된 이메일이 존재합니다.");
		}
	}

	private void saveSignUpInfo(SignUpEmailMessage message) {
		try {
			redisTemplate.opsForValue().set(message.getCode(), objectMapper.writeValueAsString(message));
			redisTemplate.expire(message.getCode(), Duration.of(message.getExpiredTime(), ChronoUnit.MILLIS));
		} catch (JsonProcessingException e) {
			throw new IllegalArgumentException(e);
		}
	}
}
