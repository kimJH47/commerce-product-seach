package back.ecommerce.domain.product;

import static back.ecommerce.domain.product.ApprovalStatus.*;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import back.ecommerce.domain.common.BaseTimeEntity;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "REQUEST_PRODUCT")
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class RequestProduct extends BaseTimeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String name;
	private String brandName;
	private Long price;
	@Enumerated(EnumType.STRING)
	private Category category;
	private ApprovalStatus approvalStatus;
	private String email;

	public static RequestProduct createWithWaitStatus(String name, String brandName, Long price, Category category,
		String email) {
		return new RequestProduct(null, name, brandName, price, category, WAIT, email);
	}
}
