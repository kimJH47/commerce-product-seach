package back.ecommerce.product.entity;

import static back.ecommerce.product.entity.ApprovalStatus.*;

import back.ecommerce.common.entity.BaseTimeEntity;
import back.ecommerce.exception.CustomException;
import back.ecommerce.exception.ErrorCode;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "REQUEST_PRODUCT")
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RequestProduct extends BaseTimeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String name;
	private String brandName;
	private Long price;
	@Enumerated(EnumType.STRING)
	private Category category;
	@Enumerated(EnumType.STRING)
	private ApprovalStatus approvalStatus;
	private String email;

	public void updateApproval(ApprovalStatus approvalStatus) {
		if (this.approvalStatus.equals(approvalStatus)) {
			throw new CustomException(ErrorCode.ALREADY_UPDATE_APPROVAL_STATUS);
		}
		this.approvalStatus = approvalStatus;
	}

	public Product toProduct() {
		return new Product(null, name, brandName, price, category);
	}

	public Long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getBrandName() {
		return brandName;
	}

	public Long getPrice() {
		return price;
	}

	public Category getCategory() {
		return category;
	}

	public ApprovalStatus getApprovalStatus() {
		return approvalStatus;
	}

	public String getEmail() {
		return email;
	}

	public static RequestProduct createWithWaitStatus(String name, String brandName, Long price, Category category,
		String email) {
		return new RequestProduct(null, name, brandName, price, category, WAIT, email);
	}

}
