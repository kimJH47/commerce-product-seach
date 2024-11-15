package back.ecommerce.api.payment.v2

import back.ecommerce.api.payment.KakaoReadyPaymentResult
import back.ecommerce.api.payment.OrderProductDto
import back.ecommerce.api.spec.ApiTestSpec
import back.ecommerce.api.support.*
import back.ecommerce.auth.domain.AuthUser
import back.ecommerce.auth.domain.Role
import back.ecommerce.client.KakaoPaymentClient
import back.ecommerce.order.application.OrderService
import back.ecommerce.order.service.OrderGroupDto
import back.ecommerce.payment.dto.request.PaymentReadyRequest
import back.ecommerce.payment.service.PaymentService
import com.fasterxml.jackson.databind.ObjectMapper
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.verify
import org.hamcrest.CoreMatchers.equalTo
import org.springframework.http.MediaType
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user
import org.springframework.test.web.servlet.MockMvc
import java.time.LocalDateTime
import java.util.*

class PaymentV2ControllerTest(
    private val kakaoPaymentClient: KakaoPaymentClient,
    private val paymentService: PaymentService,
    private val orderService: OrderService,
    private val mockMvc: MockMvc,
    private val objectMapper: ObjectMapper
) : ApiTestSpec({

    describe("/api/v2/payment/ready POST") {
        context("유효한 요청이 오면") {
            val products = listOf(
                OrderProductDto(100L, "상품A", 1, 10000),
                OrderProductDto(101L, "상품B", 2, 5000)
            )
            val request = PaymentReadyRequest("kmr2644@gmail.com", 3, 20000, products)
            val orderCode = UUID.randomUUID()
            val time = LocalDateTime.now().withNano(0)
            val authUser = AuthUser("kmr2644@gmail.com", Role.MEMBER)
            every { orderService.createOrder(any(), any(), any()) } returns
                    OrderGroupDto(orderCode.toString(), "상품 A 등 총 2개의 상품", 3, 20000)
            every { kakaoPaymentClient.ready(any(), any(), any(), any(), any()) } returns
                    KakaoReadyPaymentResult(
                        "appUrl", "mobileUrl", "pcUrl", "12345", "12345", orderCode.toString(),
                        time.toString()
                    )
            every { paymentService.createReadyPayment(any(), any(), any(), any(), any()) } just Runs

            it("return 200") {
                mockMvc.docPost("/api/v2/payment/ready") {
                    contentType = MediaType.APPLICATION_JSON
                    content = objectMapper.writeValueAsString(request)
                    header("Authorization", "Bearer your_token")
                    with(user(authUser))
                }.andExpect {
                    status { isOk() }
                    jsonPath("$.message", equalTo("결제준비가 완료 되었습니다."))
                    jsonPath("$.entity.pcUrl", equalTo("pcUrl"))
                    jsonPath("$.entity.orderCode", equalTo(orderCode.toString()))
                    jsonPath("$.entity.createdAt", equalTo(time.toString()))
                }.andDocument("PC 결제 준비 API") {
                    requestHeaders("Authorization" to "토큰 인증 헤더")
                    requestFields(
                        "email" type STRING means "이메일",
                        "totalCount" type NUMBER means "상품 전체 갯수",
                        "totalPrice" type NUMBER means "상품 전체 금액",
                        "orderProducts" type ARRAY means "결제 상품 데이터",
                        "orderProducts[0].productId" type NUMBER means "상품 ID",
                        "orderProducts[0].name" type STRING means "상품 이름",
                        "orderProducts[0].quantity" type NUMBER means "상품 갯수",
                        "orderProducts[0].price" type NUMBER means "상품 가격",
                    )
                    responseFields(
                        "message" type STRING means "응답 메시지",
                        "entity.pcUrl" type STRING means "PC 결제 URL",
                        "entity.orderCode" type STRING means "주문 번호",
                        "entity.createdAt" type DATETIME means "주문 생성 시간",
                    )
                }
                verify(exactly = 1) { orderService.createOrder(any(), any(), any())  }
                verify(exactly = 1) { kakaoPaymentClient.ready(any(), any(), any(), any(), any())  }
                verify(exactly = 1) { paymentService.createReadyPayment(any(), any(), any(), any(), any())  }
            }
        }

    }

})
