package back.ecommerce.user.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import back.ecommerce.user.entity.SignUpInfo;

public interface SignUpRepository extends JpaRepository<SignUpInfo, Long> {
	Optional<SignUpInfo> findByVerifiedCode(String verifiedCode);

	boolean existsByEmail(String email);
}
