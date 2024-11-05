package back.ecommerce.api.cart

import back.ecommerce.api.spec.ApiTestSpec
import back.ecommerce.api.support.*
import back.ecommerce.cart.application.CartService
import back.ecommerce.cart.dto.request.AddCartRequest
import back.ecommerce.cart.dto.response.AddCartResponse
import com.fasterxml.jackson.databind.ObjectMapper
import io.mockk.every
import io.mockk.verify
import org.hamcrest.CoreMatchers.equalTo
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc

class CartV2ControllerTest(
    mockMvc: MockMvc,
    objectMapper: ObjectMapper,
    cartService: CartService
) : ApiTestSpec(
    {
        describe("/api/v2/cart/add-product POST") {
            context("유효한 요청이 오면") {
                every { cartService.addProduct(any(), any(), any()) } returns AddCartResponse(1000L, 10, 10000L)
                it("return 200") {
                    mockMvc.docPost("/api/v2/cart/add-product") {
                        contentType = MediaType.APPLICATION_JSON
                        content = objectMapper.writeValueAsString(AddCartRequest("test@gmail.com", 10L, 10))
                    }.andExpect {
                        status { isOk() }
                        jsonPath("$.message", equalTo("장바구니에 상품이 추가 되었습니다."))
                        jsonPath("$.entity.id", equalTo(1000))
                        jsonPath("$.entity.quantity", equalTo(10))
                        jsonPath("$.entity.price", equalTo(10000))
                    }.andDocument("상품 장바구니 등록 API") {
                        requestFields(
                            "email" type STRING means "이메일",
                            "productId" type NUMBER means "상품 ID",
                            "quantity" type NUMBER means "상품 갯수",
                        )
                        responseFields(
                            "message" type STRING means "응답 메시지",
                            "entity.id" type NUMBER means "장바구니 ID",
                            "entity.quantity" type NUMBER means "장바구니 상품 총 수량",
                            "entity.price" type NUMBER means "상품 총 가격"
                        )
                    }
                    verify(exactly = 1) { cartService.addProduct(any(), any(), any()) }
                }
            }
        }
    }
)