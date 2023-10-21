package back.ecommerce.repository.user;

import java.util.Optional;

import back.ecommerce.domain.user.SignUpInfo;

public interface SignUpRepository {

	SignUpInfo save(SignUpInfo signUpInfo);

	Optional<SignUpInfo> findByVerifiedCode(String verifiedCode);
}
