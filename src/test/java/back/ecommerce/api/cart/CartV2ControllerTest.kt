package back.ecommerce.api.cart

import back.ecommerce.api.spec.ApiTestSpec
import back.ecommerce.api.support.*
import back.ecommerce.cart.application.CartService
import back.ecommerce.cart.dto.request.AddCartRequest
import back.ecommerce.cart.dto.request.DeleteCartRequest
import back.ecommerce.cart.dto.response.*
import back.ecommerce.product.entity.Category
import com.fasterxml.jackson.databind.ObjectMapper
import io.mockk.every
import io.mockk.verify
import org.hamcrest.CoreMatchers.equalTo
import org.springframework.http.HttpHeaders.AUTHORIZATION
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

        describe("GET /api/v2/cart") {
            context("유효한 요청이 오면") {

                val email = "kmr2644@gmail.com"
                val dtos = listOf(
                    CartProductDto(1000L, "item1", "brand1", 10000L, Category.OUTER, 1),
                    CartProductDto(1001L, "item2", "brand2", 500L, Category.ACCESSORY, 2)
                )
                val cartListResponse = CartListResponse(email, CartProducts.create(dtos))

                every { cartService.findCartByUserEmail(any()) } returns cartListResponse

                it("return 200") {
                    mockMvc.docGet("/api/v2/cart") {
                        header(AUTHORIZATION, "Bearer your_token")
                    }.andExpect {
                        status { isOk() }
                        jsonPath("$.message", equalTo("장바구니가 성공적으로 조회 되었습니다."))
                        jsonPath("$.entity.email", equalTo("kmr2644@gmail.com"))
                        jsonPath("$.entity.cartProducts.count", equalTo(3))
                        jsonPath("$.entity.cartProducts.totalPrice", equalTo(11000))
                        jsonPath("$.entity.cartProducts.size()", equalTo(2))

                        jsonPath("$.entity.cartProducts.value[0].id", equalTo(1000))
                        jsonPath("$.entity.cartProducts.value[0].name", equalTo("item1"))
                        jsonPath("$.entity.cartProducts.value[0].brandName", equalTo("brand1"))
                        jsonPath("$.entity.cartProducts.value[0].price", equalTo(10000))
                        jsonPath("$.entity.cartProducts.value[0].category", equalTo("OUTER"))
                        jsonPath("$.entity.cartProducts.value[0].quantity", equalTo(1))

                        jsonPath("$.entity.cartProducts.value[1].id", equalTo(1001))
                        jsonPath("$.entity.cartProducts.value[1].name", equalTo("item2"))
                        jsonPath("$.entity.cartProducts.value[1].brandName", equalTo("brand2"))
                        jsonPath("$.entity.cartProducts.value[1].price", equalTo(1000))
                        jsonPath("$.entity.cartProducts.value[1].category", equalTo("ACCESSORY"))
                        jsonPath("$.entity.cartProducts.value[1].quantity", equalTo(2))
                    }.andDocument("장바구니 조회 API") {
                        requestHeaders("Authorization" to "토큰 인증 헤더")
                        responseFields(
                            "message" type STRING means "응답 메시지",
                            "entity.email" type STRING means "이메일",
                            "entity.cartProducts.count" type NUMBER means "장바구니 전체 상품 수",
                            "entity.cartProducts.totalPrice" type NUMBER means "장바구니 전체 가격",
                            "entity.cartProducts.value" type ARRAY means "장바구니 상품 데이터",
                            "entity.cartProducts.value[].id" type NUMBER means "장바구니 상품 ID",
                            "entity.cartProducts.value[].name" type STRING means "장바구니 상품 이름",
                            "entity.cartProducts.value[].brandName" type STRING means "장바구니 브랜드 이름",
                            "entity.cartProducts.value[].price" type NUMBER means "장바구니 상품 가격",
                            "entity.cartProducts.value[].category" type STRING means "장바구니 상품 카테고리",
                            "entity.cartProducts.value[].quantity" type NUMBER means "장바구니 상품 갯수",
                        )
                    }

                    verify(exactly = 1) { cartService.addProduct(any(), any(), any()) }
                }
            }

            describe("DELETE /api/v2/cart") {
                context("유효한 요청이 오면") {
                    val request = DeleteCartRequest("kmr2644@gmail.com", 100L)
                    val response = CartDeleteResponse("kmr2644@gmail.com", 100L)

                    every { cartService.deleteById(any(), any()) } returns response

                    it("should not return 200") {
                        mockMvc.docDelete("/api/v2/cart") {
                            header(AUTHORIZATION, "Bearer your_token")
                            contentType = MediaType.APPLICATION_JSON
                            content = objectMapper.writeValueAsString(request)
                        }.andExpect {
                            status { isOk() }
                            jsonPath("$.message", equalTo("상품이 성공적으로 삭제되었습니다."))
                            jsonPath("$.entity.email", equalTo("kmr2644@gmail.com"))
                            jsonPath("$.entity.id", equalTo(100))
                        }.andDocument("장바구니 상품 삭제 API") {
                            requestHeaders("Authorization" to "토큰 인증 헤더")
                            requestFields(
                                "email" type STRING means "이메일",
                                "cartId" type NUMBER means "장바구니 상품 ID"
                            )
                            responseFields(
                                "message" type STRING means "응답 메시지",
                                "entity.email" type STRING means "이메일",
                                "entity.id" type NUMBER means "장바구니 상품 ID"
                            )
                        }
                        verify(exactly = 1) { cartService.deleteById(any(), any()) }
                    }
                }
            }
        }
    }
)