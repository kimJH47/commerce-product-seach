package back.ecommerce.api.admin

import back.ecommerce.admin.dto.request.AddRequestProductRequest
import back.ecommerce.admin.dto.request.UpdateApprovalRequest
import back.ecommerce.admin.dto.response.AddRequestProductResponse
import back.ecommerce.admin.dto.response.RequestProductDto
import back.ecommerce.admin.dto.response.UpdateApprovalStatusDto
import back.ecommerce.admin.service.AdminService
import back.ecommerce.api.spec.ApiTestSpec
import back.ecommerce.api.support.*
import back.ecommerce.product.entity.ApprovalStatus
import back.ecommerce.product.entity.Category
import back.ecommerce.publisher.aws.EmailSQSEventPublisher
import back.ecommerce.publisher.aws.MessageType
import com.fasterxml.jackson.databind.ObjectMapper
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.verify
import org.hamcrest.CoreMatchers.equalTo
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.post
import java.time.LocalDateTime

class AdminV2ControllerTest(
    mockMvc: MockMvc,
    objectMapper: ObjectMapper,
    adminService: AdminService,
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
        describe("GET /api/v2/admin/add-request-product") {
            context("유효한 요청이 들어오면") {
                val now = LocalDateTime.now().withNano(0)
                val dto = RequestProductDto(
                    1000L,
                    "kmr2644@gmail.com",
                    "상품",
                    "브랜드",
                    Category.OUTER,
                    1500000L,
                    ApprovalStatus.WAIT,
                    now
                )
                every { adminService.findByApprovalStatus(ApprovalStatus.WAIT) } returns listOf(dto)

                it("return 200") {
                    mockMvc.docGet("/api/v2/admin/add-request-product") {
                        header("Authorization", "Bearer your_token")
                    }.andExpect {
                        status { isOk() }
                        jsonPath("$.message", equalTo("등록요청 상품이 성공적 조회 되었습니다."))
                        jsonPath("$.entity.size()", equalTo(1))
                        jsonPath("$.entity[0].requestId", equalTo(1000))
                        jsonPath("$.entity[0].email", equalTo("kmr2644@gmail.com"))
                        jsonPath("$.entity[0].name", equalTo("상품"))
                        jsonPath("$.entity[0].brandName", equalTo("브랜드"))
                        jsonPath("$.entity[0].category", equalTo("OUTER"))
                        jsonPath("$.entity[0].price", equalTo(1500000))
                        jsonPath("$.entity[0].approvalStatus", equalTo("WAIT"))
                        jsonPath("$.entity[0].requestTime", equalTo(now.toString()))
                    }.andDocument("등록 요청 상품 조회 API") {
                        requestHeaders("Authorization" to "토큰 인증 헤더")
                        responseFields(
                            "message" type STRING means "응답 메시지",
                            "entity[0].requestId" type NUMBER means "등록 요청 상품 ID",
                            "entity[0].email" type STRING means "등록 요청 상품 요청자 이메일",
                            "entity[0].name" type STRING means "등록 요청 상품 이름",
                            "entity[0].brandName" type STRING means "등록 요청 상품 브랜드",
                            "entity[0].category" type STRING means "등록 요청 상품 카테고리",
                            "entity[0].price" type NUMBER means "등록 요청 상품 가격",
                            "entity[0].approvalStatus" type STRING means "등록 요청 상품 상태",
                            "entity[0].requestTime" type DATETIME means "등록 요청 상품 요청 시간"
                        )
                    }

                }

            }
        }

        describe("POST api/v2/admin/update-approval") {
            context("유효한 요청이 오면") {
                val request = UpdateApprovalRequest(1000L, ApprovalStatus.SUCCESS, "kmr2644@gmail.com")

                val approvalStatusDto =
                    UpdateApprovalStatusDto("kmr2644@gmail.com", 1000L, ApprovalStatus.SUCCESS)

                every {
                    adminService.updateApprovalStatus(
                        any(),
                        any(ApprovalStatus::class),
                        any()
                    )
                } returns approvalStatusDto

                every { emailSQSEventPublisher.pub(any(MessageType::class), any()) } just Runs

                it("return 200") {
                    mockMvc.post("/api/v2/admin/update-approval") {
                        header("Authorization", "Bearer your_token")
                        contentType = MediaType.APPLICATION_JSON
                        content = objectMapper.writeValueAsString(request)
                    }.andExpect {
                        status { isOk() }
                        jsonPath("$.message", equalTo("등록요청 상품이 성공적으로 업데이트 되었습니다."))
                        jsonPath("$.entity.email", equalTo("kmr2644@gmail.com"))
                        jsonPath("$.entity.requestId", equalTo(1000))
                        jsonPath("$.entity.approvalStatus", equalTo("SUCCESS"))
                    }.andDocument("등록 요청상품 승인상태 업데이트 API") {
                        requestHeaders("Authorization" to "토큰 인증 헤더")
                        requestFields(
                            "requestId" type NUMBER means "등록 요청상품 ID",
                            "approvalStatus" type STRING means "업데이트 할 승인상태",
                            "email" type STRING means "상품 등록 요청자 이메일"
                        )
                        responseFields(
                            "message" type STRING means "응답 메시지",
                            "entity.email" type STRING means "상품 등록 요청자 이메일",
                            "entity.requestId" type NUMBER means "등록 요청상품 ID",
                            "entity.approvalStatus" type STRING means "업데이트 된 승인요청 상태",
                        )
                    }
                    verify(exactly = 1) { adminService.updateApprovalStatus(any(), any(), any()) }
                    verify(exactly = 1) { emailSQSEventPublisher.pub(any(), any()) }
                }
            }
        }
    }
)