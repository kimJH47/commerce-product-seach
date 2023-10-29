package back.ecommerce.product.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import back.ecommerce.product.entity.ApprovalStatus;
import back.ecommerce.product.entity.RequestProduct;

public interface RequestProductRepository extends JpaRepository<RequestProduct, Long> {

	List<RequestProduct> findByApprovalStatus(ApprovalStatus status);
}
