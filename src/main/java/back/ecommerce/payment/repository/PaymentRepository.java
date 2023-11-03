package back.ecommerce.payment.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import back.ecommerce.payment.entity.Payment;

public interface PaymentRepository extends JpaRepository<Payment,Long> {
	Optional<Payment> findByOrderCode(String orderCode);
}
