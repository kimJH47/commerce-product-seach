package back.ecommerce.api.admin.v2

import back.ecommerce.admin.dto.request.AddRequestProductRequest
import back.ecommerce.admin.dto.request.UpdateApprovalRequest
import back.ecommerce.admin.service.AdminService
import back.ecommerce.api.dto.Response
import back.ecommerce.product.entity.ApprovalStatus
import back.ecommerce.publisher.aws.EmailSQSEventPublisher
import back.ecommerce.publisher.aws.MessageType.REQUEST_PRODUCT_APPROVAL_STATUS
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v2/admin")
class AdminV2Controller(
    private val adminService: AdminService,
    private val emailSQSEventPublisher: EmailSQSEventPublisher
) {
    @PostMapping("/add-request-product")
    fun addRequestProduct(@RequestBody @Valid request: AddRequestProductRequest): ResponseEntity<Response> {
        return Response.createSuccessResponse(
            "상품등록 요청이 성공적으로 요청되었습니다.", adminService.addRequestProduct(request))
    }

    @GetMapping("/add-request-product")
    fun findWaitApprovalProduct(): ResponseEntity<Response> {
        return Response.createSuccessResponse(
            "등록요청 상품이 성공적 조회 되었습니다.", adminService.findByApprovalStatus(ApprovalStatus.WAIT))
    }

    @PostMapping("/admin/update-approval")
    fun updateApprovalProduct(@RequestBody @Valid request: UpdateApprovalRequest): ResponseEntity<Response> {
        val status = adminService.updateApprovalStatus(request.requestId, request.approvalStatus, request.email)
        emailSQSEventPublisher.pub(REQUEST_PRODUCT_APPROVAL_STATUS, status.toMap())
        return Response.createSuccessResponse("등록요청 상품이 성공적으로 업데이트 되었습니다.", status)
    }

}