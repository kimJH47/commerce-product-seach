package back.ecommerce.repository;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import back.ecommerce.domain.User;

@DataJpaTest
class UserRepositoryTest {
	@Autowired
	UserRepository userRepository;


	@Test
	@DisplayName("사용자의 이메일로 조회가 성공적으로 되어야한다.")
	void findByEmail() {
		//given
		User expected = new User(1L,"email1@email.com","dsdaml!edmslm");
		User user2 = new User(2L,"emall2@email.com","sddmmdlskamlk12das");
		User user3 = new User(3L,"email3@email.com","sddmmdlskamlk13");
		userRepository.save(expected);
		userRepository.save(user2);
		userRepository.save(user3);

	    //when
		User actual = userRepository.findByEmail("email1@email.com")
			.orElse(new User(-1000L, "", ""));

		//then
		assertThat(actual).isEqualTo(expected);


	}
}