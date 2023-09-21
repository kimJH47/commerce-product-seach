package back.ecommerce.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import back.ecommerce.domain.product.RequestProduct;

public interface RequestProductRepository extends JpaRepository<RequestProduct, Long> {

}
