package back.ecommerce.user.repository;

import java.util.Optional;

import back.ecommerce.user.entity.SignUpInfo;

public interface SignUpRepository {

	SignUpInfo save(SignUpInfo signUpInfo);

	Optional<SignUpInfo> findByVerifiedCode(String verifiedCode);
}
