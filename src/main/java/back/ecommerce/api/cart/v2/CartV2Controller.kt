package back.ecommerce.api.cart.v2

import back.ecommerce.api.dto.Response
import back.ecommerce.auth.annotaion.UserEmail
import back.ecommerce.cart.dto.request.AddCartRequest
import back.ecommerce.cart.dto.request.DeleteCartRequest
import back.ecommerce.cart.service.CartService
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("api/v2/cart")
class CartV2Controller(
    private val cartService: CartService,
) {

    @PostMapping("/add-product")
    fun addCart(@RequestBody @Valid request: AddCartRequest): ResponseEntity<Response> {
        return Response.createSuccessResponse(
            "장바구니에 상품이 추가 되었습니다.",
            cartService.addProduct(request.email, request.productId, request.quantity)
        )
    }

    @GetMapping
    fun findByEmail(@UserEmail email: String): ResponseEntity<Response> {
        return Response.createSuccessResponse(
            "장바구니가 성공적으로 조회 되었습니다.",
            cartService.findCartByUserEmail(email)
        )
    }

    @DeleteMapping
    fun deleteById(@RequestBody request: DeleteCartRequest): ResponseEntity<Response> {
        return Response.createSuccessResponse(
            "상품이 성공적으로 삭제되었습니다.",
            cartService.deleteById(request.cartId, request.email)
        )
    }

}