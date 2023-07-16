package back.ecommerce.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import back.ecommerce.domain.Cart;

public interface CartRepository extends JpaRepository<Cart, Long> {
	List<Cart> findByUserEmail(String userEmail);
}
