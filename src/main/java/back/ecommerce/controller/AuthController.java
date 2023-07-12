package back.ecommerce.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import back.ecommerce.dto.request.LoginRequest;
import back.ecommerce.dto.response.Response;
import back.ecommerce.service.AuthService;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

	private final AuthService authService;


	@PostMapping("/token")
	public ResponseEntity<Response> login(@RequestBody LoginRequest loginRequest) {
		return Response.createSuccessResponse("인증이 성공적으로 완료되었습니다.",
			authService.createToken(loginRequest.getEmail(),loginRequest.getPassword()));
	}
}
