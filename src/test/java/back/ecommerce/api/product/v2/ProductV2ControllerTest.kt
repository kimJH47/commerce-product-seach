package back.ecommerce.api.product.v2

import back.ecommerce.api.spec.ApiTestSpec
import back.ecommerce.api.support.*
import back.ecommerce.product.application.ProductV2Service
import back.ecommerce.product.dto.response.v2.ProductDetailDto
import back.ecommerce.product.dto.response.v2.ProductListV2Response
import back.ecommerce.product.dto.response.v2.ProductV2Dto
import back.ecommerce.product.entity.Category
import io.mockk.every
import io.mockk.verify
import org.hamcrest.CoreMatchers.equalTo
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath

class ProductV2ControllerTest(
    mockMvc: MockMvc,
    productService: ProductV2Service

) : ApiTestSpec({
    describe("GET /api/v2/categories/{category}") {
        context("유효한 요청이 오면") {

            val products = ArrayList<ProductV2Dto>()
            products.add(createDto(1L, "옷A", "브랜드A", 10000L, Category.TOP, "http://via.placeholder.com/640x480"))
            products.add(createDto(10L, "옷B", "브랜드B", 15000L, Category.TOP, "http://via.placeholder.com/640x480"))
            products.add(createDto(15L, "옷C", "브랜드D", 170010L, Category.TOP, "http://via.placeholder.com/640x480"))
            val response = ProductListV2Response(products.size, products)

            every { productService.findWithCategoryAndPagination(any(Category::class)) } returns response

            it("return 200") {
                mockMvc.docGet("/api/v2/categories/{category}", "TOP")
                    .andExpect {
                        status { isOk() }
                        jsonPath("$.message", equalTo("상품이 성공적으로 조회 되었습니다."))
                        jsonPath("$.entity.totalCount").value(3)
                        jsonPath("$.entity.products[0].name").value("옷A")
                        jsonPath("$.entity.products[0].brandName").value("브랜드A")
                        jsonPath("$.entity.products[0].price").value(10000L)
                        jsonPath("$.entity.products[0].category").value("TOP")
                        jsonPath("$.entity.products[0].thumbnailUrl").value("http://via.placeholder.com/640x480")

                        jsonPath("$.entity.products[1].name").value("옷B")
                        jsonPath("$.entity.products[1].brandName").value("브랜드B")
                        jsonPath("$.entity.products[1].price").value(15000L)
                        jsonPath("$.entity.products[1].category").value("TOP")
                        jsonPath("$.entity.products[1].thumbnailUrl").value("http://via.placeholder.com/640x480")

                        jsonPath("$.entity.products[2].name").value("옷C")
                        jsonPath("$.entity.products[2].brandName").value("브랜드D")
                        jsonPath("$.entity.products[2].price").value(170010L)
                        jsonPath("$.entity.products[2].category").value("TOP")
                        jsonPath("$.entity.products[2].thumbnailUrl").value("http://via.placeholder.com/640x480")

                    }.andDocument("카테고리 상품 조회 첫 페이지 조회 API") {
                        pathParameters("category" to "카테고리")
                        responseFields(
                            "message" type STRING means "응답 메시지",
                            "entity.totalCount" type NUMBER means "조회된 상품 총 갯수",
                            "entity.products" type ARRAY means "조회된 상품 데이터",
                            "entity.products[0].id" type NUMBER means "상품 ID",
                            "entity.products[0].name" type STRING means "상품 이름",
                            "entity.products[0].brandName" type STRING means "상품 브랜드 이름",
                            "entity.products[0].price" type NUMBER means "상품가격",
                            "entity.products[0].category" type STRING means "상품 카테고리",
                            "entity.products[0].thumbnailUrl" type STRING means "상품 썸네일 URL",
                        )
                    }
                verify(exactly = 1) { productService.findWithCategoryAndPagination(any(Category::class)) }
            }
        }
    }
    describe("GET /api/v2/categories/{category}/detail") {
        context("유효한 요청이 오면") {
            val products = ArrayList<ProductV2Dto>()
            products.add(createDto(1L, "옷A", "brand", 10000L, Category.TOP, "http://via.placeholder.com/640x480"))
            products.add(createDto(10L, "옷B", "brand", 15000L, Category.TOP, "http://via.placeholder.com/640x480"))

            val response = ProductListV2Response(products.size, products)

            every { productService.findWithSearchCondition(any(Category::class), any()) } returns response

            it("return 200") {
                mockMvc.docGet("/api/v2/categories/{category}/detail", "TOP") {
                    param("sort", "new")
                    param("page", "1")
                    param("name", "옷")
                }.andExpect {
                    status { isOk() }
                    jsonPath("$.message", equalTo("상품이 성공적으로 조회 되었습니다."))
                    jsonPath("$.entity.totalCount").value(2)
                    jsonPath("$.entity.products.size()").value(2)
                    jsonPath("$.entity.products[0].name").value("옷A")
                    jsonPath("$.entity.products[0].brandName").value("brand")
                    jsonPath("$.entity.products[0].price").value(10000L)
                    jsonPath("$.entity.products[0].category").value("TOP")
                    jsonPath("$.entity.products[0].thumbnailUrl").value("http://via.placeholder.com/640x480")

                    jsonPath("$.entity.products[1].name").value("옷B")
                    jsonPath("$.entity.products[1].brandName").value("brand")
                    jsonPath("$.entity.products[1].price").value(15000L)
                    jsonPath("$.entity.products[1].category").value("TOP")
                    jsonPath("$.entity.products[1].thumbnailUrl").value("http://via.placeholder.com/640x480")
                }.andDocument("카테고리 상품 조건 조회 API") {
                    pathParameters("category" to "카테고리")
                    queryParameters(
                        "name" query STRING means "상품 이름" isRequired false isOptional true,
                        "brandName" query STRING means "상품 브랜드 이름" isRequired false isOptional true,
                        "minPrice" query STRING means "상품 최소 가격" isRequired false isOptional true,
                        "maxPrice" query STRING means "상품 최대 가격" isRequired false isOptional true,
                        "page" query STRING means "페이지 번호" isRequired true,
                        "sort" query STRING means "정렬 방식" isRequired false isOptional true,
                    )
                    responseFields(
                        "message" type STRING means "응답 메시지",
                        "entity.totalCount" type NUMBER means "조회된 상품 총 갯수",
                        "entity.products" type ARRAY means "조회된 상품 데이터",
                        "entity.products[0].id" type NUMBER means "상품 ID",
                        "entity.products[0].name" type STRING means "상품 이름",
                        "entity.products[0].brandName" type STRING means "상품 브랜드 이름",
                        "entity.products[0].price" type NUMBER means "상품가격",
                        "entity.products[0].category" type STRING means "상품 카테고리",
                        "entity.products[0].thumbnailUrl" type STRING means "상품 썸네일 URL",
                    )
                }
                verify(exactly = 1) { productService.findWithSearchCondition(any(Category::class), any()) }
            }
        }
    }

    describe("GET /api/v2/product/{id}") {
        context("유효한 요청이 오면") {
            val imageUrls = listOf("http://via.placeholder.com/500x500", "http://via.placeholder.com/500x500")
            val catalog = "http://via.placeholder.com/640x480"
            val productDetailDto = ProductDetailDto(
                100L, "상품", "브랜드", 10000L, imageUrls, catalog
            )
            every { productService.findOne(any()) } returns productDetailDto
            it("return 200") {
                mockMvc.docGet("/api/v2/product/{id}", 100)
                    .andExpect {
                        status { isOk() }
                        jsonPath("$.message", equalTo("상품이 성공적으로 조회 되었습니다."))
                        jsonPath("$.entity.id").value(100L)
                        jsonPath("$.entity.name").value("상품")
                        jsonPath("$.entity.brandName").value("브랜드")
                        jsonPath("$.entity.price").value(10000L)
                        jsonPath("$.entity.imageUrls.size()").value(2)
                        jsonPath("$.entity.imageUrls[0]").value("http://via.placeholder.com/500x500")
                        jsonPath("$.entity.catalogUrl").value("http://via.placeholder.com/640x480")
                    }.andDocument("카테고리 상품 단건 조회 API") {
                        pathParameters("id" to "상품 ID")
                        responseFields(
                            "message" type STRING means "응답 메시지",
                            "entity.id" type NUMBER means "상품 ID",
                            "entity.name" type STRING means "상품 이름",
                            "entity.brandName" type STRING means "상품 브랜드 이름",
                            "entity.price" type NUMBER means "상품가격",
                            "entity.imageUrls" type ARRAY means "상품 이미지 URL",
                            "entity.catalogUrl" type STRING means "상품 카탈로그 URL",
                        )
                    }

                verify(exactly = 1) { productService.findOne(any()) }

            }
        }
    }
})


private fun createDto(
    id: Long,
    name: String,
    brandName: String,
    price: Long,
    category: Category,
    thumnailUrl: String
): ProductV2Dto {
    return ProductV2Dto(id, name, brandName, price, category, thumnailUrl)
}