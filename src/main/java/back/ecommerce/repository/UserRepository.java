package back.ecommerce.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import back.ecommerce.domain.User;

public interface UserRepository extends JpaRepository<User,Long> {
}
