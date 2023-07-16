package back.ecommerce.controller;

import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import back.ecommerce.dto.request.AddCartRequest;
import back.ecommerce.dto.response.Response;
import back.ecommerce.service.CartService;
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
}