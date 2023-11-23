package back.ecommerce.integration;

import java.util.Map;

import back.ecommerce.publisher.aws.EmailPublisher;
import back.ecommerce.publisher.aws.MessageType;

public class TestEmailPublisher implements EmailPublisher {
	@Override
	public void pub(MessageType messageType, Map<String, String> map) {

	}
}
