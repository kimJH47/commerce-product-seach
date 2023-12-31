package back.ecommerce.order;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import back.ecommerce.order.entity.OrderGroup;

public interface OrderGroupRepository extends JpaRepository<OrderGroup,Long> {
	Optional<OrderGroup> findByOrderCode(String orderCode);
}
