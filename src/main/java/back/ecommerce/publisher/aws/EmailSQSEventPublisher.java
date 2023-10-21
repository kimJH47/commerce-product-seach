package back.ecommerce.publisher.aws;

import static java.util.stream.Collectors.*;

import java.util.Map;

import com.amazonaws.services.sqs.AmazonSQSAsync;
import com.amazonaws.services.sqs.model.MessageAttributeValue;
import com.amazonaws.services.sqs.model.SendMessageRequest;

public class EmailSQSEventPublisher {

	private static final String MESSAGE_BODY = "ecommerce email";

	private final AmazonSQSAsync amazonSQSAsync;
	private final String queueUrl;

	public EmailSQSEventPublisher(AmazonSQSAsync amazonSQSAsync, String queueUrl) {
		this.amazonSQSAsync = amazonSQSAsync;
		this.queueUrl = queueUrl;
	}

	public void pub(MessageType messageType, Map<String, String> map) {
		map.put("messageType", messageType.toString());
		map.put("messageTypeCode", messageType.getCode());
		SendMessageRequest sendMessageRequest = createMessageRequest(map);
		amazonSQSAsync.sendMessage(sendMessageRequest);
	}

	private SendMessageRequest createMessageRequest(Map<String, String> map) {
		SendMessageRequest sendMessageRequest = new SendMessageRequest(queueUrl, MESSAGE_BODY);
		Map<String, MessageAttributeValue> attributeMap = createMessageAttribute(map);
		sendMessageRequest.setMessageAttributes(attributeMap);
		return sendMessageRequest;
	}

	private Map<String, MessageAttributeValue> createMessageAttribute(Map<String, String> map) {
		return map.entrySet().stream()
			.collect(toMap(Map.Entry::getKey, entry -> new MessageAttributeValue()
				.withDataType("String")
				.withStringValue(entry.getValue())));
	}
}

