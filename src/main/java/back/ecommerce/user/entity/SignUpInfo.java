package back.ecommerce.user.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "sign_up_info")
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SignUpInfo {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String email;
	private String password;
	private String verifiedCode;
	private Long expiredTime;
	private LocalDateTime createdDate;

	public Long getId() {
		return id;
	}

	public String getEmail() {
		return email;
	}

	public String getPassword() {
		return password;
	}

	public String getVerifiedCode() {
		return verifiedCode;
	}

	public Long getExpiredTime() {
		return expiredTime;
	}

	public LocalDateTime getCreatedDate() {
		return createdDate;
	}

	public static SignUpInfo create(String verifiedCode, String email, String password, Long expiredTime) {
		return new SignUpInfo(null, email, password, verifiedCode, expiredTime, LocalDateTime.now());
	}

	public boolean isExpired() {
		return createdDate
			.plusSeconds(expiredTime)
			.isBefore(LocalDateTime.now());
	}
}
