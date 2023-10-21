package back.ecommerce.repository.user;

import org.springframework.data.jpa.repository.JpaRepository;

import back.ecommerce.domain.user.SignUpInfo;

public interface SignUpMysqlRepository extends SignUpRepository, JpaRepository<SignUpInfo, Long> {

}
