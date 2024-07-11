package back.ecommerce.api.admin

import back.ecommerce.admin.dto.request.AddRequestProductRequest
import back.ecommerce.admin.dto.response.AddRequestProductResponse
import back.ecommerce.admin.repository.AdminRepository
import back.ecommerce.admin.service.AdminService
import back.ecommerce.api.spec.ApiTestSpec
import back.ecommerce.api.support.*
import back.ecommerce.product.entity.ApprovalStatus
import back.ecommerce.publisher.aws.EmailSQSEventPublisher
import com.fasterxml.jackson.databind.ObjectMapper
import io.mockk.every
import io.mockk.verify
import org.hamcrest.CoreMatchers.equalTo
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import java.time.LocalDateTime

class AdminV2ControllerTest(
    mockMvc: MockMvc,
    objectMapper: ObjectMapper,
    adminService: AdminService,
    adminRepository: AdminRepository,
    emailSQSEventPublisher: EmailSQSEventPublisher
) : ApiTestSpec(
    {
        describe("/api/v2/admin/add-request-product POST") {
            context("유효한 요청이 들어오면") {
                val email = "test@test.com"
                val requestId = 100L
                val request = AddRequestProductRequest(email, "상품", "브랜드", 100000L, "신발")
                val requestTime = LocalDateTime.now().withNano(0)
                every { adminService.addRequestProduct(any()) } returns AddRequestProductResponse(
                    email, requestId,
                    requestTime, ApprovalStatus.WAIT
                )

                it("상태코드 200과 함께 요청상품 id 가 응답되어야 한다.") {
                    mockMvc.docPost("/api/v2/admin/add-request-product") {
                        contentType = MediaType.APPLICATION_JSON
                        content = objectMapper.writeValueAsString(request)
                    }.andExpect {
                        status { isOk() }
                        jsonPath("$.message", equalTo("상품등록 요청이 성공적으로 요청되었습니다."))
                        jsonPath("$.entity.email", equalTo("test@test.com"))
                        jsonPath("$.entity.requestId", equalTo(100))
                        jsonPath("$.entity.requestTime", equalTo(requestTime.toString()))
                        jsonPath("$.entity.approvalStatus", equalTo("WAIT"))
                    }.andDocument("상품등록 요청 API") {
                        requestFields(
                            "email" type STRING means "이메일",
                            "name" type STRING means "상품 이름",
                            "brandName" type STRING means "브랜드 이름",
                            "price" type NUMBER means "상품 가격",
                            "category" type STRING means "상품 카테고리"
                        )
                        responseFields(
                            "message" type STRING means "응답 메시지",
                            "entity.email" type STRING means "이메일",
                            "entity.requestId" type NUMBER means "등록요청 상품 ID",
                            "entity.requestTime" type DATETIME means "요청시간",
                            "entity.approvalStatus" type STRING means "요청상태"
                        )
                    }
                    verify(exactly = 1) { adminService.addRequestProduct(any()) }
                }
            }
        }
    }
)