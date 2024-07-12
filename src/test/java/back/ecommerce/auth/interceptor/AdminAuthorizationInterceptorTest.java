package back.ecommerce.auth.interceptor;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.Optional;

import back.ecommerce.api.auth.interceptor.AdminAuthorizationInterceptor;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import back.ecommerce.admin.entity.Admin;
import back.ecommerce.exception.AuthenticationException;
import back.ecommerce.admin.repository.AdminRepository;

@ExtendWith(MockitoExtension.class)
class AdminAuthorizationInterceptorTest {

	AdminAuthorizationInterceptor adminAuthorizationInterceptor;
	@Mock
	AdminRepository adminRepository;
	@Mock
	HttpServletRequest request;
	@Mock
	HttpServletResponse response;
	@Mock
	Object handle;

	@BeforeEach
	void setUp() {
		adminAuthorizationInterceptor = new AdminAuthorizationInterceptor(adminRepository);
	}

	@Test
	@DisplayName("어드민이 유효할시 정상적으로 통과되어야함")
	void handle() throws Exception {
		//given
		given(request.getAttribute(anyString())).willReturn("email@email.com");
		given(adminRepository.findByEmail(anyString()))
			.willReturn(Optional.of(new Admin(15L, "email@email.com", "password")));

		//when
		boolean actual = adminAuthorizationInterceptor.preHandle(request, response, handle);

		//then
		assertThat(actual).isTrue();
		then(adminRepository).should(times(1)).findByEmail(anyString());
		then(request).should(times(1)).getAttribute(anyString());
	}

	@Test
	@DisplayName("어드민이 유효 하지 않을 시 AuthenticationException 을 던저야함")
	void handle_admin_not_found() {
		//given
		given(request.getAttribute(anyString())).willReturn("email@email.com");
		given(adminRepository.findByEmail(anyString()))
			.willReturn(Optional.empty());

		//expect
		assertThatThrownBy(() -> adminAuthorizationInterceptor.preHandle(request, response, handle))
			.isInstanceOf(AuthenticationException.class)
			.hasMessage("권한이 없습니다.");

		then(adminRepository).should(times(1)).findByEmail(anyString());
		then(request).should(times(1)).getAttribute(anyString());

	}
}