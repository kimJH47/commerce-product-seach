package back.ecommerce.admin.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import back.ecommerce.admin.entity.Admin;

public interface AdminRepository extends JpaRepository<Admin,Long> {

	Optional<Admin> findByEmail(String email);

}
