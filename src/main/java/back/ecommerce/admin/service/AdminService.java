package back.ecommerce.admin.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import back.ecommerce.product.entity.ApprovalStatus;
import back.ecommerce.product.entity.Category;
import back.ecommerce.product.entity.RequestProduct;
import back.ecommerce.admin.dto.request.AddRequestProductRequest;
import back.ecommerce.admin.dto.response.UpdateApprovalStatusDto;
import back.ecommerce.admin.dto.response.AddRequestProductResponse;
import back.ecommerce.admin.dto.response.RequestProductDto;
import back.ecommerce.exception.CustomException;
import back.ecommerce.exception.ErrorCode;
import back.ecommerce.product.repository.RequestProductRepository;
import back.ecommerce.product.repository.ProductRepository;
import back.ecommerce.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminService {

	private final RequestProductRepository requestProductRepository;
	private final UserRepository userRepository;

	private final ProductRepository productRepository;

	@Transactional
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

	@Transactional(readOnly = true)
	public List<RequestProductDto> findByApprovalStatus(ApprovalStatus status) {
		return requestProductRepository.findByApprovalStatus(status).stream()
			.map(RequestProductDto.Companion::create)
			.collect(Collectors.toList());
	}

	@Transactional
	public UpdateApprovalStatusDto updateApprovalStatus(Long requestId, ApprovalStatus approvalStatus, String email) {
		RequestProduct requestProduct = requestProductRepository.findById(requestId)
			.orElseThrow(() -> new CustomException(ErrorCode.REQUEST_PRODUCT_NOT_FOUND));

		requestProduct.updateApproval(approvalStatus);
		if (approvalStatus.equals(ApprovalStatus.SUCCESS)) {
			productRepository.save(requestProduct.toProduct());
		}

		return new UpdateApprovalStatusDto(email, requestId, approvalStatus);
	}
}
