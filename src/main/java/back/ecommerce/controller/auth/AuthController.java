package back.ecommerce.controller.auth;

import java.time.LocalDateTime;

import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import back.ecommerce.publisher.aws.EmailSQSEventPublisher;
import back.ecommerce.dto.request.user.LoginRequest;
import back.ecommerce.dto.request.user.SignUpRequest;
import back.ecommerce.dto.response.auth.SignUpDto;
import back.ecommerce.dto.response.common.Response;
import back.ecommerce.dto.response.user.SignUpResponse;
import back.ecommerce.service.auth.AuthService;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

	private final AuthService authService;
	private final EmailSQSEventPublisher sqsEventPublisher;

	@PostMapping("/token")
	public ResponseEntity<Response> login(@RequestBody @Valid LoginRequest loginRequest) {
		return Response.createSuccessResponse("인증이 성공적으로 완료되었습니다.",
			authService.createToken(loginRequest.getEmail(), loginRequest.getPassword()));
	}

	@PostMapping("/sign-up")
	public ResponseEntity<Response> signUp(@RequestBody @Valid SignUpRequest request) {
		SignUpDto signUpDto = authService.signUp(request.getEmail(), request.getPassword());
		sqsEventPublisher.pub(signUpDto.toMap());
		return Response.createSuccessResponse("회원가입 요청이 성공적으로 완료되었습니다.",
			new SignUpResponse(signUpDto.getEmail(), LocalDateTime.now()));
	}

	@GetMapping("/verified/{code}")
	public ResponseEntity<Response> verifiedEmailCode(@PathVariable String code) {
		return Response.createSuccessResponse("이메일 인증이 성공적으로 완료되었습니다.", authService.verifiedEmailCode(code));
	}
}
