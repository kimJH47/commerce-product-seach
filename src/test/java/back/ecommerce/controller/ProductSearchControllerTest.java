package back.ecommerce.controller;

import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.ArrayList;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import back.ecommerce.domain.product.Category;
import back.ecommerce.dto.ProductDto;
import back.ecommerce.dto.response.ProductListResponse;
import back.ecommerce.service.ProductService;

@WebMvcTest(ProductSearchController.class)
@Import(MockAuthProviderConfig.class)
class ProductSearchControllerTest {

	@Autowired
	MockMvc mockMvc;
	@MockBean
	ProductService productService;

	@Test
	@DisplayName("/api/categories/{value} GET 요청을 보내면 해당되는 카테고리의 상품들이 페이징되어(항상 1페이지) 응답되어야한다.")
	void find_category() throws Exception {
		//given
		ArrayList<ProductDto> products = new ArrayList<>();
		products.add(createDto(1L, "옷A", "브랜드A", 10000L, Category.TOP));
		products.add(createDto(10L, "옷B", "브랜드B", 15000L, Category.TOP));
		products.add(createDto(15L, "옷C", "브랜드D", 170010L, Category.TOP));
		ProductListResponse productListResponse = new ProductListResponse(products.size(), products);

		given(productService.findWithCategoryAndPagination(any(Category.class)))
			.willReturn(productListResponse);

		//expect
		mockMvc.perform(get("/api/categories/top"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.message").value("상품이 성공적으로 조회 되었습니다."))
			.andExpect(jsonPath("$.entity.totalCount").value(3))
			.andExpect(jsonPath("$.entity.products[0].name").value("옷A"))
			.andExpect(jsonPath("$.entity.products[0].brandName").value("브랜드A"))
			.andExpect(jsonPath("$.entity.products[0].price").value(10000L))
			.andExpect(jsonPath("$.entity.products[0].category").value("TOP"))

			.andExpect(jsonPath("$.entity.products[1].name").value("옷B"))
			.andExpect(jsonPath("$.entity.products[1].brandName").value("브랜드B"))
			.andExpect(jsonPath("$.entity.products[1].price").value(15000L))
			.andExpect(jsonPath("$.entity.products[1].category").value("TOP"))

			.andExpect(jsonPath("$.entity.products[2].name").value("옷C"))
			.andExpect(jsonPath("$.entity.products[2].brandName").value("브랜드D"))
			.andExpect(jsonPath("$.entity.products[2].price").value(170010L))
			.andExpect(jsonPath("$.entity.products[2].category").value("TOP"));

		then(productService).should(times(1)).findWithCategoryAndPagination(any(Category.class));

	}

	@Test
	@DisplayName("api/categories/{value} GET 로 유효하지 않는 카테고리 value 를 보내면 응답코드 404와 함께 실패이유가 응답되어야한다.")
	void find_category_invalid_value() throws Exception {
		//then
		//expect
		mockMvc.perform(get("/api/categories/to3p"))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.reasons.argument").value("일치하는 카테고리가 없습니다."));
	}

	private ProductDto createDto(long id, String name, String brandName, long price, Category category) {
		return new ProductDto(id, name, brandName, price, category);
	}
}