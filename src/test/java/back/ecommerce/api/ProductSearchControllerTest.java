package back.ecommerce.api;

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

import back.ecommerce.api.product.ProductSearchController;
import back.ecommerce.product.entity.Category;
import back.ecommerce.product.dto.response.ProductDto;
import back.ecommerce.product.dto.response.ProductListResponse;
import back.ecommerce.exception.CustomException;
import back.ecommerce.exception.ErrorCode;
import back.ecommerce.product.service.ProductService;

@WebMvcTest(ProductSearchController.class)
@Import(MockMvcTestConfig.class)
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
		//expect
		mockMvc.perform(get("/api/categories/to3p"))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.reasons.category").value("유효하지 않은 카테고리명 입니다."));
	}

	@Test
	@DisplayName("api/categories/{category}/detail GET 에 쿼리스트링으로 검색 상세조건을 포함해서 요청하면 조건에 해당하는 상품이 페이징되어 응답되어야한다.")
	void findProductWithPagination() throws Exception {
		//given
		ArrayList<ProductDto> products = new ArrayList<>();
		products.add(createDto(10L, "바지A", "브랜드A", 15000L, Category.PANTS));
		products.add(createDto(13L, "바지B", "브랜드B", 13000L, Category.PANTS));
		products.add(createDto(15L, "바지C", "브랜드C", 18000L, Category.PANTS));
		products.add(createDto(20L, "바지D", "브랜드D", 10000L, Category.PANTS));
		products.add(createDto(25L, "바지E", "브랜드E", 5000L, Category.PANTS));
		ProductListResponse productListResponse = new ProductListResponse(products.size(), products);

		given(productService.findWithSearchCondition(any(Category.class), any()))
			.willReturn(productListResponse);

		//expect
		mockMvc.perform(get("/api/categories/PANTS/detail")
				.param("page", "1")
				.param("name", "")
				.param("brandName", "")
				.param("minPrice", "100")
				.param("maxPrice", "1000")
				.param("sort", "new")
			)
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.message").value("상품이 성공적으로 조회 되었습니다."))
			.andExpect(jsonPath("$.entity.totalCount").value(5))

			.andExpect(jsonPath("$.entity.products[0].name").value("바지A"))
			.andExpect(jsonPath("$.entity.products[0].brandName").value("브랜드A"))
			.andExpect(jsonPath("$.entity.products[0].price").value(15000L))
			.andExpect(jsonPath("$.entity.products[0].category").value("PANTS")
			)
			.andExpect(jsonPath("$.entity.products[1].name").value("바지B"))
			.andExpect(jsonPath("$.entity.products[1].brandName").value("브랜드B"))
			.andExpect(jsonPath("$.entity.products[1].price").value(13000L))
			.andExpect(jsonPath("$.entity.products[1].category").value("PANTS"))

			.andExpect(jsonPath("$.entity.products[2].name").value("바지C"))
			.andExpect(jsonPath("$.entity.products[2].brandName").value("브랜드C"))
			.andExpect(jsonPath("$.entity.products[2].price").value(18000L))
			.andExpect(jsonPath("$.entity.products[2].category").value("PANTS"));

		then(productService).should(times(1)).findWithSearchCondition(any(Category.class), any());
	}

	@Test
	@DisplayName("상품상세 검색시 유효하지 않는 카테고리가 uri 에 포함되면 404 응답코드와 함께 실패이유가 응답되어야 한다.")
	void find_product_detail_invalid_category() throws Exception {
		//expect
		mockMvc.perform(get("/api/categories/seamkl2/detail")
				.param("page", "1")
				.param("name", "")
				.param("brandName", "")
				.param("minPrice", "100")
				.param("maxPrice", "1000")
				.param("sort", "NEW"))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.reasons.category").value("유효하지 않은 카테고리명 입니다."));

		then(productService).should(times(0)).findWithSearchCondition(any(Category.class), any());

	}


	@Test
	@DisplayName("api/product/{id} GET 으로 단건조회 API 사용시 id 에 해당하는 상품이 응답코드 200과 함께 조회 되어야한다.")
	void find_one() throws Exception {
	    //given
		given(productService.findOne(any()))
			.willReturn(createDto(1, "상품", "브랜드", 150000, Category.ACCESSORY));

	    //expect
		mockMvc.perform(get("/api/product/1"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.entity.id").value("1"))
			.andExpect(jsonPath("$.entity.name").value("상품"))
			.andExpect(jsonPath("$.entity.brandName").value("브랜드"))
			.andExpect(jsonPath("$.entity.price").value("150000"))
			.andExpect(jsonPath("$.entity.category").value("ACCESSORY"));

		then(productService).should(times(1)).findOne(any());
	}


	@Test
	@DisplayName("api/product/{id} GET 으로 존재하지 않는 상품의 id 를 보내면 응답코드 400 과함께 실패이유가 응답되어야 한다.")
	void find_one_product_not_found() throws Exception {
	    //given
		given(productService.findOne(any()))
			.willThrow(new CustomException(ErrorCode.PRODUCT_NOT_FOUND));

	    //expect
		mockMvc.perform(get("/api/product/1"))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.message").value("잘못된 요청입니다."))
			.andExpect(jsonPath("$.reasons.product").value("해당하는 상품이 존재하지 않습니다."));

		then(productService).should(times(1)).findOne(any());


	}
	private ProductDto createDto(long id, String name, String brandName, long price, Category category) {
		return new ProductDto(id, name, brandName, price, category);
	}
}