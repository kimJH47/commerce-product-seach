package back.ecommerce.integration;

import static org.assertj.core.api.Assertions.*;

import java.util.HashMap;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;

import back.ecommerce.api.admin.AdminController;
import back.ecommerce.api.dto.Response;
import back.ecommerce.api.dto.SuccessResponse;
import back.ecommerce.api.product.ProductSearchController;
import back.ecommerce.product.dto.request.ProductSearchConditionRequest;
import back.ecommerce.product.dto.response.ProductDto;
import back.ecommerce.product.dto.response.ProductListResponse;
import back.ecommerce.product.entity.Category;
import back.ecommerce.product.entity.Product;
import back.ecommerce.product.repository.ProductRepository;
import back.ecommerce.product.service.ProductService;

@SpringBootTest
@Transactional
public class ProductApiTest {

	ProductSearchController productSearchController;
	AdminController adminController;
	@Autowired
	ProductService productService;
	@Autowired
	ProductRepository productRepository;

	@BeforeEach
	void setUp() {
		productSearchController = new ProductSearchController(productService);
		for (int i = 0; i < 30; i++) {
			productRepository.save(createProduct(null, "상의상품" + i, "brand", 100000L * i, Category.TOP));
		}
		for (int i = 0; i < 20; i++) {
			productRepository.save(createProduct(null, "신발상품", "brand", 150000L, Category.SNEAKERS));
		}
	}

	@Test
	void findCategories() {
		//when
		ResponseEntity<Response> responseResponseEntity = productSearchController.findProductWithCategory("TOP");
		SuccessResponse<ProductListResponse> body = (SuccessResponse<ProductListResponse>)responseResponseEntity.getBody();

		//then
		assertThat(responseResponseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(body.getEntity().getProducts().size()).isEqualTo(body.getEntity().getTotalCount());
		assertThat(body.getEntity().getProducts().size()).isEqualTo(20);
		assertThat(body.getEntity().getProducts()).extracting(ProductDto::getCategory).containsOnly(Category.TOP);
	}

	@Test
	void findProduct_page() throws Exception {
		//given
		HashMap<String, String> hashMap = new HashMap<>();
		hashMap.put("page", "2");
		ProductSearchConditionRequest request = ProductSearchConditionRequest.create(Category.TOP, hashMap);

		//when
		ResponseEntity<Response> responseResponseEntity = productSearchController.findProductWithPagination(request);
		SuccessResponse<ProductListResponse> body = (SuccessResponse<ProductListResponse>)responseResponseEntity.getBody();

		//then
		assertThat(responseResponseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(body.getMessage()).isEqualTo("상품이 성공적으로 조회 되었습니다.");
		assertThat(body.getEntity().getProducts().size()).isEqualTo(body.getEntity().getTotalCount());
		assertThat(body.getEntity().getProducts().size()).isEqualTo(10);
		assertThat(body.getEntity().getProducts()).extracting(ProductDto::getCategory).containsOnly(Category.TOP);

	}

	@Test
	void findOne() {
		//given
		Long id = productRepository.save(createProduct(null, "newPro", "PUMA", 150000, Category.HEAD_WEAR))
			.getId();

		//when
		ResponseEntity<Response> responseResponseEntity = productSearchController.findOne(id);
		SuccessResponse<ProductDto> body = (SuccessResponse<ProductDto>)responseResponseEntity.getBody();

		//then
		assertThat(responseResponseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(body.getMessage()).isEqualTo("상품이 성공적으로 조회 되었습니다.");
		assertThat(body.getEntity().getPrice()).isEqualTo(150000L);
		assertThat(body.getEntity().getName()).isEqualTo("newPro");
		assertThat(body.getEntity().getBrandName()).isEqualTo("PUMA");
		assertThat(body.getEntity().getCategory()).isEqualTo(Category.HEAD_WEAR);

	}

	@NotNull
	private static Product createProduct(Long id, String name, String brandName, long price, Category category) {
		return new Product(id, name, brandName, price, category);
	}
}
