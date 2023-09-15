package back.ecommerce.service.auth;

import static io.lettuce.core.pubsub.PubSubOutput.Type.*;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import back.ecommerce.domain.user.User;
import back.ecommerce.exception.EmailCodeNotFoundException;
import back.ecommerce.exception.ExistsUserEmailException;
import back.ecommerce.repository.user.UserRepository;
import lombok.AllArgsConstructor;
import lombok.Getter;
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
			throw new ExistsUserEmailException("이미 가입된 이메일이 존재합니다.");
		}
	}

	private void cachingKeyAndValue(String key, CacheValue value) {
		redisTemplate.opsForValue()
			.set(key, value.toJson(objectMapper), Duration.of(value.getExpiredTime(), ChronoUnit.MILLIS));
	}

	@Transactional
	public String verifiedCodeAndSaveUser(String code) {
		String value = redisTemplate.opsForValue().getAndDelete(code);
		if (value == null) {
			throw new EmailCodeNotFoundException("이메일 코드가 존재하지 않습니다.");
		}
		CacheValue cacheValue = CacheValue.fromJson(objectMapper, value);
		validateEmail(cacheValue.getEmail());
		User user = User.create(cacheValue.getEmail(), cacheValue.getPassword());
		userRepository.save(user);
		return cacheValue.getEmail();
	}

	@AllArgsConstructor
	@Getter
	static class CacheValue {

		private String email;
		private String password;
		private Long expiredTime;

		public String toJson(ObjectMapper mapper) {
			try {
				return mapper.writeValueAsString(mapper.writeValueAsString(message));
			} catch (JsonProcessingException e) {
				throw new IllegalArgumentException(e);
			}
		}

		public static CacheValue fromJson(ObjectMapper mapper, String json) {
			try {
				return mapper.readValue(json, CacheValue.class);
			} catch (JsonProcessingException e) {
				throw new IllegalArgumentException(e);
			}
		}
	}

}
