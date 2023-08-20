package back.ecommerce.controller.cart;

import javax.validation.Valid;
import javax.validation.constraints.Email;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import back.ecommerce.auth.annotaion.UserEmail;
import back.ecommerce.dto.request.cart.AddCartRequest;
import back.ecommerce.dto.response.common.Response;
import back.ecommerce.exception.TokenHasInvalidException;
import back.ecommerce.service.cart.CartService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

	private final CartService cartService;

	@PostMapping("/add-product")
	public ResponseEntity<Response> addCart(@RequestBody @Valid AddCartRequest request) {
		cartService.addProduct(request.getEmail(), request.getProductId(), request.getQuantity());
		return Response.createSuccessResponse("상품이 카트에 성공적으로 추가 되었습니다.",
			cartService.findCartByUserEmail(request.getEmail()));
	}

	@GetMapping
	public ResponseEntity<Response> findByEmail(
		@UserEmail String tokenEmail,
		@RequestParam("email") @Valid @Email String email) {
		if (!tokenEmail.equals(email)) {
			throw new TokenHasInvalidException("토큰정보와 요청정보가 일치하지 않습니다.");
		}
		return Response.createSuccessResponse("장바구니가 성곡적으로 조회 되었습니다.",
			cartService.findCartByUserEmail(email));
	}
}