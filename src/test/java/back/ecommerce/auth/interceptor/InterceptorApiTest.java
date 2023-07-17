package back.ecommerce.auth.interceptor;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import com.fasterxml.jackson.databind.ObjectMapper;

import back.ecommerce.auth.token.Token;
import back.ecommerce.auth.token.TokenProvider;
import back.ecommerce.domain.User;
import back.ecommerce.domain.product.Category;
import back.ecommerce.domain.product.Product;
import back.ecommerce.dto.request.AddCartRequest;
import back.ecommerce.dto.response.FailedResponse;
import back.ecommerce.repository.ProductRepository;
import back.ecommerce.repository.UserRepository;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class InterceptorApiTest {

	@Autowired
	UserRepository userRepository;
	@Autowired
	ProductRepository productRepository;
	@Autowired
	TokenProvider tokenProvider;
	@Autowired
	TestRestTemplate testRestTemplate;
	@Autowired
	ObjectMapper objectMapper;

	@LocalServerPort
	int port;

	@BeforeEach
	void setUp() {
		userRepository.save(new User(1L, "myad@email.com", "{noop}password"));
		productRepository.save(new Product(1L, "맨투맨", "브랜드", 10000L, Category.TOP));
	}

	@Test
	@DisplayName("토큰이 유효할시 api 가 성공적으로 사용되어야한다.")
	void interceptor() throws Exception {
		//given
		String email = "myad@email.com";
		Token token = tokenProvider.create(email, 100000);

		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setBearerAuth(token.getValue());
		httpHeaders.setContentType(MediaType.APPLICATION_JSON);

		HttpEntity<String> request = new HttpEntity<>(
			objectMapper.writeValueAsString(new AddCartRequest("myad@email.com", 1L, 1)), httpHeaders);

		//then
		ResponseEntity<String> response = testRestTemplate.postForEntity(
			"http://localhost:" + port + "/api/cart/add-product", request, String.class);

		//when
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
	}

	@Test
	@DisplayName("인증헤더가 null 일 때 응답코드 400 와 함께 실패이유를 응답받아야한다.")
	void authorizationHeader_null() throws Exception {
		//given
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setContentType(MediaType.APPLICATION_JSON);

		HttpEntity<String> request = new HttpEntity<>(
			objectMapper.writeValueAsString(new AddCartRequest("myad@email.com", 1L, 1)), httpHeaders);

		//then
		ResponseEntity<String> response = testRestTemplate.postForEntity(
			"http://localhost:" + port + "/api/cart/add-product", request, String.class);
		FailedResponse failedResponse = objectMapper.readValue(response.getBody(), FailedResponse.class);

		//when
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
		assertThat(failedResponse.getReasons().get("authorizationHeader")).isEqualTo("인증 헤더가 비어있습니다.");
	}

	@Test
	@DisplayName("인증헤더가 비어 있을 때 응답코드 400 와 함께 실패이유를 응답받아야한다.")
	void authorizationHeader_empty() throws Exception {
		//given
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.set("Authorization", "  ");
		httpHeaders.setContentType(MediaType.APPLICATION_JSON);

		HttpEntity<String> request = new HttpEntity<>(
			objectMapper.writeValueAsString(new AddCartRequest("myad@email.com", 1L, 1)), httpHeaders);

		//then
		ResponseEntity<String> response = testRestTemplate.postForEntity(
			"http://localhost:" + port + "/api/cart/add-product", request, String.class);
		FailedResponse failedResponse = objectMapper.readValue(response.getBody(), FailedResponse.class);

		//when
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
		assertThat(failedResponse.getReasons().get("authorizationHeader")).isEqualTo("인증 헤더가 비어있습니다.");
	}

	@Test
	@DisplayName("인증헤더의 인증타입이 일치하지 않으면 응답코드 400과 함께 실패이유를 응답 받아야한다.")
	void authorizationHeader_invalidAuthType() throws Exception {
		//given
		String email = "myad@email.com";
		Token token = tokenProvider.create(email, 100000);

		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setBasicAuth(token.getValue());
		httpHeaders.setContentType(MediaType.APPLICATION_JSON);

		HttpEntity<String> request = new HttpEntity<>(
			objectMapper.writeValueAsString(new AddCartRequest("myad@email.com", 1L, 1)), httpHeaders);

		//then
		ResponseEntity<String> response = testRestTemplate.postForEntity(
			"http://localhost:" + port + "/api/cart/add-product", request, String.class);
		FailedResponse failedResponse = objectMapper.readValue(response.getBody(), FailedResponse.class);

		//when
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
		assertThat(failedResponse.getReasons().get("authorizationHeader")).isEqualTo("인증 헤더타입이 일치하지 않습니다.");
	}

	@Test
	@DisplayName("토큰의 만료되면 응답코드 400과 함께 실패이유를 응답 받아야햔다.")
	void token_expiredTime() throws Exception {
		//given
		String email = "myad@email.com";
		Token token = tokenProvider.create(email, -10000);

		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setBearerAuth(token.getValue());
		httpHeaders.setContentType(MediaType.APPLICATION_JSON);

		HttpEntity<String> request = new HttpEntity<>(
			objectMapper.writeValueAsString(new AddCartRequest("myad@email.com", 1L, 1)), httpHeaders);

		//then
		ResponseEntity<String> response = testRestTemplate.postForEntity(
			"http://localhost:" + port + "/api/cart/add-product", request, String.class);
		FailedResponse failedResponse = objectMapper.readValue(response.getBody(), FailedResponse.class);

		//when
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
		assertThat(failedResponse.getReasons().get("accessToken")).isEqualTo("토큰이 만료 되었습니다.");
	}

	@Test
	@DisplayName("토큰이 유효하지 않으면 응답코드 400과 함께 실패이유를 응답 받아야한다.")
	void token_invalid() throws Exception {
		//given
		String email = "myad@email.com";
		Token token = tokenProvider.create(email, 10000);

		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setBearerAuth(token.getValue() + "1");
		httpHeaders.setContentType(MediaType.APPLICATION_JSON);

		HttpEntity<String> request = new HttpEntity<>(
			objectMapper.writeValueAsString(new AddCartRequest("myad@email.com", 1L, 1)), httpHeaders);

		//then
		ResponseEntity<String> response = testRestTemplate.postForEntity(
			"http://localhost:" + port + "/api/cart/add-product", request, String.class);
		FailedResponse failedResponse = objectMapper.readValue(response.getBody(), FailedResponse.class);

		//when
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
		assertThat(failedResponse.getReasons().get("accessToken")).isEqualTo("토큰이 유효하지 않습니다.");

	}
}
