package back.ecommerce.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import back.ecommerce.user.entity.SignUpInfo;

public interface SignUpMysqlRepository extends SignUpRepository, JpaRepository<SignUpInfo, Long> {

}
