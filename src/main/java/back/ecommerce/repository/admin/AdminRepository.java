package back.ecommerce.repository.admin;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import back.ecommerce.domain.admin.Admin;

public interface AdminRepository extends JpaRepository<Admin,Long> {

	Optional<Admin> findByEmail(String email);

}
