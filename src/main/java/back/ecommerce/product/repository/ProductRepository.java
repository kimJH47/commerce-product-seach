package back.ecommerce.product.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import back.ecommerce.product.entity.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {

	@Query("select p from Product p where p.id in :ids")
	List<Product> findByIds(List<Long> ids);
}
