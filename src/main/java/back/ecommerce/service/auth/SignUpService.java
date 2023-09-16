package back.ecommerce.service.auth;

import static back.ecommerce.exception.ErrorCode.*;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import back.ecommerce.domain.user.User;
import back.ecommerce.exception.CustomException;
import back.ecommerce.repository.user.UserRepository;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SignUpService {

	private final UserRepository userRepository;
	private final RedisTemplate<String, String> redisTemplate;

	private final ObjectMapper objectMapper;

	public void cachingSignUpInfo(String code, String email, String password, Long expiredTime) {
		validateEmail(email);
		cachingKeyAndValue(code, new CacheValue(email, password, expiredTime));
	}

	@Transactional(readOnly = true)
	public void validateEmail(String email) {
		if (userRepository.existsByEmail(email)) {
			throw new CustomException(DUPLICATE_USER_EMAIL);
		}
	}

	private void cachingKeyAndValue(String key, CacheValue value) {
		try {
			String json = objectMapper.writeValueAsString(value);
			redisTemplate.opsForValue()
				.set(key, json, Duration.of(value.getExpiredTime(), ChronoUnit.MILLIS));
		} catch (JsonProcessingException e) {
			throw new IllegalArgumentException(e);
		}
	}

	@Transactional
	public String verifiedCodeAndSaveUser(String code) {
		String value = getAndDelete(code);
		redisTemplate.delete(code);
		try {
			CacheValue cacheValue = objectMapper.readValue(value, CacheValue.class);
			validateEmail(cacheValue.getEmail());
			User user = User.create(cacheValue.getEmail(), cacheValue.getPassword());
			userRepository.save(user);
			return cacheValue.getEmail();

		} catch (JsonProcessingException e) {
			throw new IllegalArgumentException(e);
		}
	}

	private String getAndDelete(String key) {
		String value = redisTemplate.opsForValue().get(key);
		if (value == null) {
			throw new CustomException(EMAIL_CODE_NOT_FOUND);
		}
		redisTemplate.delete(key);
		return value;

	}

	@AllArgsConstructor
	@NoArgsConstructor
	@Getter
	static class CacheValue {
		private String email;
		private String password;
		private Long expiredTime;
	}

}
