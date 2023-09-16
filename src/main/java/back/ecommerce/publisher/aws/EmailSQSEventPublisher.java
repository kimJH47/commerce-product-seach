package back.ecommerce.publisher.aws;

import java.util.HashMap;

import com.amazonaws.services.sqs.AmazonSQSAsync;
import com.amazonaws.services.sqs.model.MessageAttributeValue;
import com.amazonaws.services.sqs.model.SendMessageRequest;

public class EmailSQSEventPublisher {

	private static final String MESSAGE_BODY = "request sign-up email and access code";

	private final AmazonSQSAsync amazonSQSAsync;
	private final String queueUrl;

	public EmailSQSEventPublisher(AmazonSQSAsync amazonSQSAsync, String queueUrl) {
		this.amazonSQSAsync = amazonSQSAsync;
		this.queueUrl = queueUrl;
	}

	public void pub(String email, String code) {
		SendMessageRequest sendMessageRequest = createMessageRequest(email, code);
		amazonSQSAsync.sendMessage(sendMessageRequest);
	}

	private SendMessageRequest createMessageRequest(String email, String code) {
		SendMessageRequest sendMessageRequest = new SendMessageRequest(queueUrl, MESSAGE_BODY);
		HashMap<String, MessageAttributeValue> attributeMap = createMessageAttribute(email, code);
		sendMessageRequest.setMessageAttributes(attributeMap);
		return sendMessageRequest;
	}

	private HashMap<String, MessageAttributeValue> createMessageAttribute(String email, String code) {
		HashMap<String, MessageAttributeValue> attributeMap = new HashMap<>();
		attributeMap.put("email", new MessageAttributeValue()
			.withDataType("String")
			.withStringValue(email));
		attributeMap.put("code", new MessageAttributeValue()
			.withDataType("String")
			.withStringValue(code));
		return attributeMap;
	}
}

