package back.ecommerce.product.entity;

import back.ecommerce.common.entity.BaseTimeEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "PRODUCT")
public class Product extends BaseTimeEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String name;
	private String brandName;
	private Long price;
	@Enumerated(EnumType.STRING)
	private Category category;

	public Product(Long id, String name, String brandName, Long price, Category category) {
		this.id = id;
		this.name = name;
		this.brandName = brandName;
		this.price = price;
		this.category = category;
	}

	public Product() {

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
}
