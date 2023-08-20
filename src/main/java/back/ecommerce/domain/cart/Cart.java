package back.ecommerce.domain.cart;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import back.ecommerce.domain.common.BaseTimeEntity;
import back.ecommerce.domain.product.Product;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "CART")
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class Cart extends BaseTimeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "product_id")
	private Product product;
	private String userEmail;
	private int quantity;
	private long price;

	public long getPrice() {
		return price;
	}

	public Long getId() {
		return id;
	}

	public int getQuantity() {
		return quantity;
	}

	public Product getProduct() {
		return product;
	}

	public static Cart create(String userEmail, Product product, int quantity) {
		return Cart.builder()
			.userEmail(userEmail)
			.product(product)
			.price(quantity * product.getPrice())
			.quantity(quantity)
			.build();
	}
}
