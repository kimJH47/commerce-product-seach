package back.ecommerce.controller.admin;

import static back.ecommerce.publisher.aws.MessageType.*;

import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import back.ecommerce.domain.product.ApprovalStatus;
import back.ecommerce.dto.request.amdin.AddRequestProductRequest;
import back.ecommerce.dto.request.amdin.UpdateApprovalRequest;
import back.ecommerce.dto.response.admin.UpdateApprovalStatusDto;
import back.ecommerce.dto.response.common.Response;
import back.ecommerce.publisher.aws.EmailSQSEventPublisher;
import back.ecommerce.service.admin.AdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class AdminController {

	private final AdminService adminService;
	private final EmailSQSEventPublisher emailSQSEventPublisher;

	@PostMapping("/add-request-product")
	public ResponseEntity<Response> addRequestProduct(@RequestBody @Valid AddRequestProductRequest request) {
		return Response.createSuccessResponse("상품등록 요청이 성공적으로 요청되었습니다.",
			adminService.addRequestProduct(request));
	}

	@GetMapping("/admin/add-request-product")
	public ResponseEntity<Response> findWaitApprovalProduct() {
		return Response.createSuccessResponse("등록요청 상품이 성공적 조회 되었습니다.",
			adminService.findByApprovalStatus(ApprovalStatus.WAIT));
	}

	@PostMapping("/admin/update-approval")
	public ResponseEntity<Response> updateRequestProduct(@RequestBody @Valid UpdateApprovalRequest request) {
		UpdateApprovalStatusDto updateApprovalStatusDto = adminService.updateApprovalStatus(request.getRequestId(),
			request.getApprovalStatus(), request.getEmail());
		emailSQSEventPublisher.pub(REQUEST_PRODUCT_APPROVAL_STATUS, updateApprovalStatusDto.toMap());
		return Response.createSuccessResponse("등록요청 상품이 성공적으로 업데이트 되었습니다.", updateApprovalStatusDto);
	}
}
