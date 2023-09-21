package back.ecommerce.service.admin;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import back.ecommerce.domain.product.ApprovalStatus;
import back.ecommerce.domain.product.Category;
import back.ecommerce.domain.product.RequestProduct;
import back.ecommerce.dto.request.amdin.AddRequestProductRequest;
import back.ecommerce.dto.response.admin.AddRequestProductResponse;
import back.ecommerce.dto.response.admin.RequestProductDto;
import back.ecommerce.exception.CustomException;
import back.ecommerce.exception.ErrorCode;
import back.ecommerce.repository.RequestProductRepository;
import back.ecommerce.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminService {

	private final RequestProductRepository requestProductRepository;
	private final UserRepository userRepository;

	public AddRequestProductResponse addRequestProduct(AddRequestProductRequest request) {

		Category category = Category.from(request.getCategory());
		validateUserEmail(request.getEmail());

		RequestProduct requestProduct = requestProductRepository.save(RequestProduct.createWithWaitStatus(
			request.getName(),
			request.getBrandName(),
			request.getPrice(),
			category,
			request.getEmail()));

		return new AddRequestProductResponse(
			requestProduct.getEmail(), requestProduct.getId(), LocalDateTime.now(), requestProduct.getApprovalStatus());
	}

	private void validateUserEmail(String email) {
		userRepository.findByEmail(email)
			.orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
	}

	public List<RequestProductDto> findByApprovalStatus(ApprovalStatus status) {
		return null;
	}
}
