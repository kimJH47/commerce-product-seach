package back.ecommerce.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import back.ecommerce.domain.product.ApprovalStatus;
import back.ecommerce.domain.product.RequestProduct;

public interface RequestProductRepository extends JpaRepository<RequestProduct, Long> {

	List<RequestProduct> findByApprovalStatus(ApprovalStatus status);
}
