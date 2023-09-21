package back.ecommerce.controller.admin;

import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.stream.Stream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import back.ecommerce.controller.MockMvcTestConfig;
import back.ecommerce.domain.product.ApprovalStatus;
import back.ecommerce.dto.request.amdin.AddRequestProductRequest;
import back.ecommerce.dto.response.admin.AddRequestProductResponse;
import back.ecommerce.service.admin.AdminService;

@WebMvcTest(AdminController.class)
@Import(MockMvcTestConfig.class)
class AdminControllerTest {

	@MockBean
	AdminService adminService;
	@Autowired
	MockMvc mvc;
	@Autowired
	ObjectMapper mapper;

	@Test
	@DisplayName("/add-request-product POST 로 상품 등록 요청을 보내면 응답코드 200과 함께 상품등록 요청이 성공적으로 보내져야한다.")
	void add_request_product() throws Exception {
		//given
		String email = "email@email.com";
		AddRequestProductRequest request = new AddRequestProductRequest(email, "name", "Brand", 10000L,
			"accessory");
		LocalDateTime now = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);

		given(adminService.addRequestProduct(any(AddRequestProductRequest.class)))
			.willReturn(new AddRequestProductResponse(email, 200L, now, ApprovalStatus.WAIT));

		//expect
		mvc.perform(post("/api/add-request-product")
				.contentType(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsString(request)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.message").value("상품등록 요청이 성공적으로 요청되었습니다."))
			.andExpect(jsonPath("$.entity.email").value("email@email.com"))
			.andExpect(jsonPath("$.entity.productId").value(200))
			.andExpect(jsonPath("$.entity.requestTime").value(now.toString()))
			.andExpect(jsonPath("$.entity.approvalStatus").value("WAIT"));

		then(adminService).should(times(1)).addRequestProduct(any(AddRequestProductRequest.class));
	}

	@ParameterizedTest
	@MethodSource("invalidAddProductRequestProvider")
	@DisplayName("/add-request-product POST 로 유효하지 않는 상품등록 요청 데이터를 보내면 응답코드 400과 함께 실패이유가 응답 되어야한다.")
	void add_request_product_invalid_request(AddRequestProductRequest request, String reasonField) throws Exception {
		//expect
		mvc.perform(post("/api/add-request-product")
				.contentType(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsString(request)))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.message").value("잘못된 요청입니다."))
			.andExpect(jsonPath("$.reasons." + reasonField).isNotEmpty());

	}

	public static Stream<Arguments> invalidAddProductRequestProvider() {
		return Stream.of(Arguments.of(new AddRequestProductRequest(null,"name","Brand",10000L,"TOP"), "email"),
			Arguments.of(new AddRequestProductRequest("as@ffkdklj2@as","NamePr","BRAND2",100000L,"top"), "email"),
			Arguments.of(new AddRequestProductRequest("asffkdklj2@as.com","","BRAND2",100000L,"top"), "name"),
			Arguments.of(new AddRequestProductRequest("tray@as.com","product","",100000L,"top"), "brandName"),
			Arguments.of(new AddRequestProductRequest("user@as.com","product","BRAND",null,"pants"), "price"),
			Arguments.of(new AddRequestProductRequest("user@as.com","product","BRAND",0L,"top"), "price"),
			Arguments.of(new AddRequestProductRequest("kim@as.com","product","BD",100000L,""), "category")
		);
	}



}