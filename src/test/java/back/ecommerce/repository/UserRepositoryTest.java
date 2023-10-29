package back.ecommerce.repository;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import back.ecommerce.user.entity.User;
import back.ecommerce.user.repository.UserRepository;

@DataJpaTest
class UserRepositoryTest {
	@Autowired
	UserRepository userRepository;

	@Test
	@DisplayName("사용자의 이메일로 조회가 성공적으로 되어야한다.")
	void findByEmail() {
		//given
		User expected = userRepository.save(new User(10L, "email1@email.com", "dsdaml!edmslm"));
		userRepository.save(new User(null, "emall2@email.com", "sddmmdlskamlk12das"));
		userRepository.save(new User(null, "email3@email.com", "sddmmdlskamlk13"));

		//when
		User actual = userRepository.findByEmail("email1@email.com")
			.orElse(new User(-1000L, "", ""));

		//then
		assertThat(actual).isEqualTo(expected);
	}

	@Test
	@DisplayName("사용자의 이메일이 존재하는지 하고 존재하면 true 를 반환해야한다.")
	void existsByEmail_true() {
		//given
		String email = "email1@email.com";
		userRepository.save(new User(1L, email, "dsdaml!edmss,xmlm"));

		//expect
		assertThat(userRepository.existsByEmail(email)).isTrue();
	}

	@Test
	@DisplayName("사용자의 이메일이 존재하는지 하고 존재하지 않으면 false 를 반환해야한다.")
	void existsByEmail_false() {
		//given
		userRepository.save(new User(1L, "email1@email.com", "dsdamlmss,xmlm"));

		//expect
		assertThat(userRepository.existsByEmail("other@email.com")).isFalse();

	}
}