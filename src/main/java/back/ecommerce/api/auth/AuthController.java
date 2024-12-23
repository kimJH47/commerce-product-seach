package back.ecommerce.api.auth;

import static back.ecommerce.publisher.aws.MessageType.*;

import java.time.LocalDateTime;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import back.ecommerce.api.dto.Response;
import back.ecommerce.auth.dto.request.LoginRequest;
import back.ecommerce.auth.dto.request.SignUpRequest;
import back.ecommerce.auth.dto.response.SignUpDto;
import back.ecommerce.auth.dto.response.SignUpResponse;
import back.ecommerce.auth.service.AuthService;
import back.ecommerce.publisher.aws.EmailPublisher;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

	private final AuthService authService;
	private final EmailPublisher sqsEventPublisher;

	public AuthController(AuthService authService, EmailPublisher sqsEventPublisher) {
		this.authService = authService;
		this.sqsEventPublisher = sqsEventPublisher;
	}

	@PostMapping("/token")
	public ResponseEntity<Response> login(@RequestBody @Valid LoginRequest loginRequest) {
		return Response.createSuccessResponse("인증이 성공적으로 완료되었습니다.",
			authService.createToken(loginRequest.getEmail(), loginRequest.getPassword()));
	}

	@PostMapping("/sign-up")
	public ResponseEntity<Response> signUp(@RequestBody @Valid SignUpRequest request) {
		SignUpDto signUpDto = authService.signUp(request.getEmail(), request.getPassword());
		sqsEventPublisher.pub(VERIFIED_CODE, signUpDto.toMap());
		return Response.createSuccessResponse("회원가입 요청이 성공적으로 완료되었습니다.",
			new SignUpResponse(signUpDto.getEmail(), LocalDateTime.now()));
	}

	@GetMapping("/verified/{code}")
	public ResponseEntity<Response> verifyEmailCode(@PathVariable String code) {
		return Response.createSuccessResponse("이메일 인증이 성공적으로 완료되었습니다.", authService.verifyEmailCode(code));
	}
}
