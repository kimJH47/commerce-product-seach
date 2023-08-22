package back.ecommerce.controller.cart;

import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import back.ecommerce.auth.annotaion.UserEmail;
import back.ecommerce.dto.request.cart.AddCartRequest;
import back.ecommerce.dto.response.common.Response;
import back.ecommerce.service.cart.CartService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

	private final CartService cartService;

	@PostMapping("/add-product")
	public ResponseEntity<?> addCart(@RequestBody @Valid AddCartRequest request) {
		return ResponseEntity.ok()
			.body(cartService.addProduct(request.getEmail(), request.getProductId(), request.getQuantity()));
	}

	@GetMapping
	public ResponseEntity<Response> findByEmail(
		@UserEmail String tokenEmail) {
		return Response.createSuccessResponse("장바구니가 성공적으로 조회 되었습니다.",
			cartService.findCartByUserEmail(tokenEmail));
	}
}