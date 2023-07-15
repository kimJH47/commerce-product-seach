package back.ecommerce.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import back.ecommerce.domain.product.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {

}
