package back.ecommerce.repository;

import static back.ecommerce.product.entity.ApprovalStatus.*;
import static back.ecommerce.product.entity.Category.*;
import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import back.ecommerce.config.jpa.JpaAuditingConfig;
import back.ecommerce.product.entity.ApprovalStatus;
import back.ecommerce.product.entity.Category;
import back.ecommerce.product.entity.RequestProduct;
import back.ecommerce.product.repository.RequestProductRepository;

@DataJpaTest
@Import(JpaAuditingConfig.class)
class RequestProductRepositoryTest {

	@Autowired
	RequestProductRepository requestProductRepository;

	@BeforeEach
	void setUp() {
		requestProductRepository.save(createEntity("반지", "BRAND", 100000L, ACCESSORY, WAIT, "email@nave.com"));
		requestProductRepository.save(createEntity("맨투맨", "BRAND2", 90000L, TOP, WAIT, "email@nave.com"));
		requestProductRepository.save(createEntity("볼캡", "BRAND3", 150000L, HEAD_WEAR, WAIT, "email@nave.com"));

		requestProductRepository.save(createEntity("반지1", "BRAND5", 10000L, ACCESSORY, FAILED, "email@nave.com"));
		requestProductRepository.save(createEntity("반지2", "BRAND5", 80000L, ACCESSORY, FAILED, "kim@nave.com"));
		requestProductRepository.save(createEntity("반지3", "BRAND5", 90000L, ACCESSORY, FAILED, "email@nave.com"));

		requestProductRepository.save(createEntity("바지", "br1", 15000L, PANTS, SUCCESS, "email@nave.com"));
		requestProductRepository.save(createEntity("원피스", "br3", 100000L, ONEPIECE, SUCCESS, "tray@nave.com"));
		requestProductRepository.save(createEntity("원피스", "br3", 8900L, ONEPIECE, SUCCESS, "tray@nave.com"));

	}

	@ParameterizedTest
	@EnumSource(ApprovalStatus.class)
	@DisplayName("ApprovalStatus 를 인자로받아서 해당하는 등록요청 상품들이 조회 되어야한다.")
	void findByApprovalStatus(ApprovalStatus approvalStatus) {
		//when
		List<RequestProduct> products = requestProductRepository.findByApprovalStatus(approvalStatus);

		//then
		assertThat(products)
			.extracting(RequestProduct::getApprovalStatus)
			.filteredOn(status -> status.equals(approvalStatus))
			.hasSize(3);

	}

	@NotNull
	private static RequestProduct createEntity(String name, String brand, long price, Category category,
		ApprovalStatus approvalStatus, String email) {
		return new RequestProduct(null, name, brand, price, category, approvalStatus, email);
	}

}