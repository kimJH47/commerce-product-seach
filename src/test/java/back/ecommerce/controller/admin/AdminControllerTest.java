package back.ecommerce.controller.admin;

import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.fasterxml.jackson.databind.ObjectMapper;

import back.ecommerce.controller.MockMvcTestConfig;
import back.ecommerce.domain.admin.Admin;
import back.ecommerce.domain.product.ApprovalStatus;
import back.ecommerce.domain.product.Category;
import back.ecommerce.dto.request.amdin.AddRequestProductRequest;
import back.ecommerce.dto.request.amdin.UpdateApprovalRequest;
import back.ecommerce.dto.response.admin.AddRequestProductResponse;
import back.ecommerce.dto.response.admin.RequestProductDto;
import back.ecommerce.dto.response.admin.UpdateApprovalStatusDto;
import back.ecommerce.publisher.aws.EmailSQSEventPublisher;
import back.ecommerce.publisher.aws.MessageType;
import back.ecommerce.repository.admin.AdminRepository;
import back.ecommerce.service.admin.AdminService;

@WebMvcTest(value = AdminController.class, excludeFilters = {
	@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = WebMvcConfigurer.class)})
@Import(MockMvcTestConfig.class)
class AdminControllerTest {

	@MockBean
	AdminService adminService;
	@MockBean
	EmailSQSEventPublisher sqsEventPublisher;
	@MockBean
	AdminRepository adminRepository;
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
			.andExpect(jsonPath("$.entity.requestId").value(200))
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
		return Stream.of(Arguments.of(new AddRequestProductRequest(null, "name", "Brand", 10000L, "TOP"), "email"),
			Arguments.of(new AddRequestProductRequest("as@ffkdklj2@as", "NamePr", "BRAND2", 100000L, "top"), "email"),
			Arguments.of(new AddRequestProductRequest("asffkdklj2@as.com", "", "BRAND2", 100000L, "top"), "name"),
			Arguments.of(new AddRequestProductRequest("tray@as.com", "product", "", 100000L, "top"), "brandName"),
			Arguments.of(new AddRequestProductRequest("user@as.com", "product", "BRAND", null, "pants"), "price"),
			Arguments.of(new AddRequestProductRequest("user@as.com", "product", "BRAND", 0L, "top"), "price"),
			Arguments.of(new AddRequestProductRequest("kim@as.com", "product", "BD", 100000L, ""), "category")
		);
	}

	@Test
	@DisplayName("/api/admin/add-request-product GET 요청 시 승인 대기중인 등록요청 상품들이 조회되어야 한다.")
	void findWaitApprovalProduct() throws Exception {
		//given
		LocalDateTime now = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
		List<RequestProductDto> products = List.of(
			new RequestProductDto(10L, "email@naver.com", "name1", "Brand1", Category.TOP, 100500L, ApprovalStatus.WAIT,
				now),
			new RequestProductDto(15L, "email3@naver.com", "name2", "Brand5", Category.PANTS, 250000L,
				ApprovalStatus.WAIT, now));

		given(adminService.findByApprovalStatus(ApprovalStatus.WAIT))
			.willReturn(products);

		//expect
		mvc.perform(get("/api/admin/add-request-product"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.message").value("등록요청 상품이 성공적 조회 되었습니다."))
			.andExpect(jsonPath("$.entity[0].requestId").value(10))
			.andExpect(jsonPath("$.entity[0].email").value("email@naver.com"))
			.andExpect(jsonPath("$.entity[0].name").value("name1"))
			.andExpect(jsonPath("$.entity[0].brandName").value("Brand1"))
			.andExpect(jsonPath("$.entity[0].category").value("TOP"))
			.andExpect(jsonPath("$.entity[0].price").value(100500L))
			.andExpect(jsonPath("$.entity[0].approvalStatus").value("WAIT"))
			.andExpect(jsonPath("$.entity[0].requestTime").value(now.toString()))

			.andExpect(jsonPath("$.entity[1].requestId").value(15))
			.andExpect(jsonPath("$.entity[1].email").value("email3@naver.com"))
			.andExpect(jsonPath("$.entity[1].name").value("name2"))
			.andExpect(jsonPath("$.entity[1].brandName").value("Brand5"))
			.andExpect(jsonPath("$.entity[1].category").value("PANTS"))
			.andExpect(jsonPath("$.entity[1].price").value(250000L))
			.andExpect(jsonPath("$.entity[1].approvalStatus").value("WAIT"))
			.andExpect(jsonPath("$.entity[1].requestTime").value(now.toString()));

		then(adminService).should(times(1)).findByApprovalStatus(any(ApprovalStatus.class));
	}

	@Test
	@DisplayName("/api/admin/update-approval")
	void update_approval() throws Exception {
		//given
		UpdateApprovalStatusDto updateApprovalStatusDto = new UpdateApprovalStatusDto("email@email.com", 150L,
			ApprovalStatus.FAILED);
		UpdateApprovalRequest request = new UpdateApprovalRequest(150L, ApprovalStatus.FAILED,
			"email@email.com");

		given(adminService.updateApprovalStatus(anyLong(), any(ApprovalStatus.class), anyString()))
			.willReturn(updateApprovalStatusDto);

		given(adminRepository.findByEmail(anyString())).willReturn(
			Optional.of(new Admin(10L, "dsad@email.com", "pass")));

		//then
		mvc.perform(post("/api/admin/update-approval")
				.contentType(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsString(request)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.message").value("등록요청 상품이 성공적으로 업데이트 되었습니다."))
			.andExpect(jsonPath("$.entity.email").value("email@email.com"))
			.andExpect(jsonPath("$.entity.requestId").value("150"))
			.andExpect(jsonPath("$.entity.approvalStatus").value("FAILED"));

		then(adminService).should(times(1)).updateApprovalStatus(anyLong(), any(ApprovalStatus.class), anyString());
		then(sqsEventPublisher).should(times(1)).pub(any(MessageType.class), anyMap());

	}
}