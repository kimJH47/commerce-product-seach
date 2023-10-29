package back.ecommerce.cart.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import back.ecommerce.cart.entity.Cart;

public interface CartRepository extends JpaRepository<Cart, Long> {

	@Query("select c from Cart c join fetch c.product where c.userEmail =:userEmail")
	List<Cart> findByUserEmail(@Param("userEmail") String userEmail);
}
