package back.ecommerce.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
	AUTH_HEADER_IS_EMPTY(400, "authHeader", "인증 헤더가 비어있습니다."),
	AUTH_HEADER_INVALID(400, "authHeader", "인증 헤더타입이 일치하지 않습니다."),

	TOKEN_IS_EMPTY(400, "token", "토큰이 비어있습니다."),
	TOKEN_HAS_INVALID(400, "token", "토큰이 유효하지 않습니다."),
	TOKEN_HAS_EXPIRED(400, "token", "토큰이 만료 되었습니다."),

	PASSWORD_NOT_MATCHED(400, "password", "비밀번호가 일치하지 않습니다."),

	PRODUCT_NOT_FOUND(400, "product", "해당하는 상품이 존재하지 않습니다."),
	USER_NOT_FOUND(400, "user", "해당하는 유저가 존재하지 않습니다."),
	EMAIL_CODE_NOT_FOUND(400, "emailCode", "이메일 코드가 존재하지 않습니다."),
	DUPLICATE_USER_EMAIL(400, "email", "이미 가입된 이메일이 존재합니다."),
	INVALID_CATEGORY(400, "category", "유효하지 않은 카테고리명 입니다."),
	INVALID_PAGE_NUMBER(400, "page", "유효하지 않는 페이지 번호입니다."),

	INTERNAL_SERVER_ERROR(500, "server", "서버에 에러가 발생 했습니다."),
	;

	private final int status;
	private final String reasonField;
	private final String description;

}
