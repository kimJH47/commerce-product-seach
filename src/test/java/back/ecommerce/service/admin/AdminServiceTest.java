package back.ecommerce.service.admin;

import static back.ecommerce.domain.product.ApprovalStatus.*;
import static back.ecommerce.domain.product.Category.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import back.ecommerce.domain.product.ApprovalStatus;
import back.ecommerce.domain.product.Category;
import back.ecommerce.domain.product.RequestProduct;
import back.ecommerce.domain.user.User;
import back.ecommerce.dto.request.amdin.AddRequestProductRequest;
import back.ecommerce.dto.response.admin.AddRequestProductResponse;
import back.ecommerce.dto.response.admin.RequestProductDto;
import back.ecommerce.dto.response.admin.UpdateApprovalStatusDto;
import back.ecommerce.exception.CustomException;
import back.ecommerce.repository.RequestProductRepository;
import back.ecommerce.repository.product.ProductRepository;
import back.ecommerce.repository.user.UserRepository;

@ExtendWith(MockitoExtension.class)
class AdminServiceTest {

	@InjectMocks
	AdminService adminService;
	@Mock
	RequestProductRepository requestProductRepository;
	@Mock
	UserRepository userRepository;
	@Mock
	ProductRepository productRepository;

	@Test
	@DisplayName("상품등록 요청 정보가 저장되고 저장 정보를 반환해야한다.")
	void addRequestProduct() {
		//given
		String email = "tray@gmal.com";
		String name = "name";
		String brand = "brand";
		long price = 112345L;
		String category = "top";

		AddRequestProductRequest request = new AddRequestProductRequest(email, name, brand, price,
			category);

		given(requestProductRepository.save(any(RequestProduct.class)))
			.willReturn(new RequestProduct(150L, name, brand, price, TOP, WAIT, email));

		given(userRepository.findByEmail(anyString()))
			.willReturn(Optional.of(new User(10L, "tray@gmal.com", "11455")));

		//when
		AddRequestProductResponse actual = adminService.addRequestProduct(request);

		//then
		assertThat(actual.getRequestId()).isEqualTo(150L);
		assertThat(actual.getApprovalStatus()).isEqualTo(WAIT);
		assertThat(actual.getEmail()).isEqualTo("tray@gmal.com");

		then(userRepository).should(times(1)).findByEmail(anyString());
		then(requestProductRepository).should(times(1)).save(any(RequestProduct.class));
	}

	@Test
	@DisplayName("등록하는 유저가 존재하지 않으면 예외가 발생한다.")
	void addProduct_user_not_found() {
		//given
		String email = "tray@gmal.com";
		String name = "name";
		String brand = "brand";
		long price = 112345L;
		String category = "top";

		AddRequestProductRequest request = new AddRequestProductRequest(email, name, brand, price,
			category);

		given(userRepository.findByEmail(anyString()))
			.willReturn(Optional.empty());

		//expect
		assertThatThrownBy(() -> adminService.addRequestProduct(request))
			.isInstanceOf(CustomException.class)
			.hasMessage("해당하는 유저가 존재하지 않습니다.");

		then(userRepository).should(times(1)).findByEmail(anyString());
		then(requestProductRepository).should(times(0)).save(any(RequestProduct.class));
	}

	@Test
	@DisplayName("카테고리가 유효하지 않으면 예외가 발생한다.")
	void addProduct_invalid_category() {
		//given
		String email = "tray@gmal.com";
		String name = "name";
		String brand = "brand";
		long price = 112345L;
		String category = "top@";

		AddRequestProductRequest request = new AddRequestProductRequest(email, name, brand, price,
			category);

		//expect
		assertThatThrownBy(() -> adminService.addRequestProduct(request))
			.isInstanceOf(CustomException.class)
			.hasMessage("유효하지 않은 카테고리명 입니다.");

		then(userRepository).should(times(0)).findByEmail(anyString());
		then(requestProductRepository).should(times(0)).save(any(RequestProduct.class));
	}

	@ParameterizedTest
	@EnumSource(value = ApprovalStatus.class)
	@DisplayName("승인상태를 받아서 상태에 해당하는 등록요청 상품들이 반환되어야 한다.")
	void findByApprovalStatus(ApprovalStatus approvalStatus) {
		//given
		List<RequestProduct> waits = List.of(
			createRequestProduct(10L, "email@mail.com", "NAME", "Brand", TOP, 156000L, approvalStatus),
			createRequestProduct(10L, "email@mail.com", "NAME", "Brand", PANTS, 156000L, approvalStatus),
			createRequestProduct(10L, "email@mail.com", "NAME", "Brand", ACCESSORY, 156000L, approvalStatus),
			createRequestProduct(10L, "email@mail.com", "NAME", "Brand", OUTER, 156000L, approvalStatus)
		);

		given(requestProductRepository.findByApprovalStatus(any(ApprovalStatus.class)))
			.willReturn(waits);

		//when
		List<RequestProductDto> waitActual = adminService.findByApprovalStatus(approvalStatus);

		//then
		assertThat(waitActual)
			.extracting(RequestProductDto::getApprovalStatus)
			.filteredOn(status -> status.equals(approvalStatus))
			.hasSize(4);

		then(requestProductRepository).should(times(1)).findByApprovalStatus(any(ApprovalStatus.class));
	}

	public static RequestProduct createRequestProduct(long requestId, String email, String name, String brandName,
		Category category, long price,
		ApprovalStatus approvalStatus) {
		return new RequestProduct(requestId, name, brandName, price, category, approvalStatus, email);
	}

	@Test
	@DisplayName("Failed 로 등록요청 승인상태가 업데이트 되어야한다.")
	void updateApprovalStatus() {
		//given
		RequestProduct requestProduct = new RequestProduct(100L, "name", "brand", 10000L, TOP, WAIT, "email@naver.com");
		given(requestProductRepository.findById(anyLong()))
			.willReturn(Optional.of(requestProduct));
		//when
		UpdateApprovalStatusDto actual = adminService.updateApprovalStatus(100L, FAILED, "email@naver.com");

		//then
		assertThat(actual.getApprovalStatus()).isEqualTo(FAILED);
		assertThat(actual.getRequestId()).isEqualTo(100L);
		assertThat(actual.getEmail()).isEqualTo("email@naver.com");

		then(requestProductRepository).should(times(1)).findById(anyLong());
		then(productRepository).should(times(0)).save(any());
	}

	@Test
	@DisplayName("success 로 등록요청 승인상태가 업데이트 되고 상품이 저장되어야 한다.")
	void updateApprovalStatus_and_save_product() {
		//given
		RequestProduct requestProduct = new RequestProduct(100L, "name", "brand", 10000L, TOP, WAIT, "email@naver.com");
		given(requestProductRepository.findById(anyLong()))
			.willReturn(Optional.of(requestProduct));

		//when
		UpdateApprovalStatusDto actual = adminService.updateApprovalStatus(100L, SUCCESS, "email@naver.com");

		//then
		assertThat(actual.getApprovalStatus()).isEqualTo(SUCCESS);
		assertThat(actual.getRequestId()).isEqualTo(100L);
		assertThat(actual.getEmail()).isEqualTo("email@naver.com");

		then(requestProductRepository).should(times(1)).findById(anyLong());
		then(productRepository).should(times(1)).save(any());
	}

	@Test
	@DisplayName("승인요청 상품이 존재하지 않으면 예외가 발생한다.")
	void updateApprovalStatus_request_product_not_found() {
		//given
		given(requestProductRepository.findById(anyLong()))
			.willReturn(Optional.empty());

		//expect
		assertThatThrownBy(() -> adminService.updateApprovalStatus(100L, SUCCESS, "email@naver.com"))
			.isInstanceOf(CustomException.class)
			.hasMessage("등록요청 상품이 존재하지 않습니다.");

		then(requestProductRepository).should(times(1)).findById(anyLong());
		then(productRepository).should(times(0)).save(any());
	}

	@Test
	@DisplayName("업데이트 하려는 승인상태가 같으면 예외가 발생한다.")
	void updateApprovalStatus_ALREADY_UPDATE_APPROVAL_STATUS() {
		//given
		RequestProduct requestProduct = new RequestProduct(100L, "name", "brand", 10000L, TOP, FAILED,
			"email@naver.com");
		given(requestProductRepository.findById(anyLong()))
			.willReturn(Optional.of(requestProduct));

		//expect
		assertThatThrownBy(() -> adminService.updateApprovalStatus(100L, FAILED, "email@naver.com"))
			.isInstanceOf(CustomException.class)
			.hasMessage("이미 번경된 등록승인 상태 입니다.");

		then(requestProductRepository).should(times(1)).findById(anyLong());
		then(productRepository).should(times(0)).save(any());

	}

}