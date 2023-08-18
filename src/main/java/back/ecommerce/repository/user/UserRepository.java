package back.ecommerce.repository.user;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import back.ecommerce.domain.user.User;

public interface UserRepository extends JpaRepository<User,Long> {
	Optional<User> findByEmail(String email);

	boolean existsByEmail(String email);
}