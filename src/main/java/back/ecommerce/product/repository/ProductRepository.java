package back.ecommerce.product.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import back.ecommerce.product.entity.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {

}
