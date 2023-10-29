package back.ecommerce.user.repository;

import static back.ecommerce.exception.ErrorCode.*;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import back.ecommerce.user.entity.SignUpInfo;
import back.ecommerce.exception.CustomException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class SignUpRedisRepository implements SignUpRepository {

	private final RedisTemplate<String, String> redisTemplate;
	private final ObjectMapper objectMapper;

	@Override
	public SignUpInfo save(SignUpInfo signUpInfo) {
		cachingKeyAndValue(signUpInfo.getVerifiedCode(), CacheValue.create(signUpInfo));
		return signUpInfo;
	}

	private void cachingKeyAndValue(String key, CacheValue value) {
		try {
			String json = objectMapper.writeValueAsString(value);
			redisTemplate.opsForValue().set(key, json, Duration.of(value.getExpiredTime(), ChronoUnit.SECONDS));
		} catch (JsonProcessingException e) {
			throw new IllegalArgumentException(e);

		}
	}

	@Override
	public Optional<SignUpInfo> findByVerifiedCode(String verifiedCode) {
		try {
			String value = getAndDelete(verifiedCode);
			CacheValue cacheValue = objectMapper.readValue(value, CacheValue.class);
			return Optional.of(cacheValue.toEntity());

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
		private String verifiedCode;
		private String email;
		private String password;
		private Long expiredTime;

		public SignUpInfo toEntity() {
			return SignUpInfo.create(verifiedCode, email, password, expiredTime);
		}

		public static CacheValue create(SignUpInfo signUpInfo) {
			return new CacheValue(signUpInfo.getVerifiedCode(), signUpInfo.getEmail(), signUpInfo.getPassword(),
				signUpInfo.getExpiredTime());
		}
	}
}
