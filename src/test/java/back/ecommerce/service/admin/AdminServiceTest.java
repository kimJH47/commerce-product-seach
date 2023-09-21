package back.ecommerce.service.admin;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import back.ecommerce.domain.product.ApprovalStatus;
import back.ecommerce.domain.product.Category;
import back.ecommerce.domain.product.RequestProduct;
import back.ecommerce.domain.user.User;
import back.ecommerce.dto.request.amdin.AddRequestProductRequest;
import back.ecommerce.dto.response.admin.AddRequestProductResponse;
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
			.willReturn(new RequestProduct(150L, name, brand, price, Category.TOP, ApprovalStatus.WAIT, email));

		given(userRepository.findByEmail(anyString()))
			.willReturn(Optional.of(new User(10L, "tray@gmal.com", "11455")));

		//when
		AddRequestProductResponse actual = adminService.addRequestProduct(request);

		//then
		assertThat(actual.getRequestId()).isEqualTo(150L);
		assertThat(actual.getApprovalStatus()).isEqualTo(ApprovalStatus.WAIT);
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

}