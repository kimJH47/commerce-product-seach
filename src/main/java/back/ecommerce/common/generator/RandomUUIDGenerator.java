package back.ecommerce.common.generator;

import java.util.UUID;

import org.springframework.stereotype.Component;

@Component
public class RandomUUIDGenerator {

	public String create() {
		return UUID.randomUUID().toString();
	}
}
