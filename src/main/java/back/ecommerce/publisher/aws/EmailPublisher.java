package back.ecommerce.publisher.aws;

import java.util.Map;

public interface EmailPublisher {
	void pub(MessageType messageType, Map<String, String> map);
}
