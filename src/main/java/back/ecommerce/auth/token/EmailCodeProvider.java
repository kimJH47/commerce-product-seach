package back.ecommerce.auth.token;

import java.util.UUID;

import org.springframework.stereotype.Component;

@Component
public class EmailCodeProvider {


	public String create() {
		return UUID.randomUUID().toString();
	}
}