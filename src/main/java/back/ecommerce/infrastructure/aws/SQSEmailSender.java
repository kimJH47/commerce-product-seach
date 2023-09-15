package back.ecommerce.infrastructure.aws;

import java.util.HashMap;

import com.amazonaws.services.sqs.AmazonSQSAsync;
import com.amazonaws.services.sqs.model.MessageAttributeValue;
import com.amazonaws.services.sqs.model.SendMessageRequest;

import back.ecommerce.service.auth.email.EmailSender;
import back.ecommerce.service.auth.email.SignUpEmailMessage;

public class SQSEmailSender implements EmailSender {

	private static final String MESSAGE_BODY = "request sign-up email and access code";
	private final AmazonSQSAsync amazonSQSAsync;

	private final String queueUrl;

	public SQSEmailSender(AmazonSQSAsync amazonSQSAsync, String queueUrl) {
		this.amazonSQSAsync = amazonSQSAsync;
		this.queueUrl = queueUrl;
	}

	@Override
	public void send(SignUpEmailMessage message) {
		SendMessageRequest sendMessageRequest = createMessageRequest(message);
		amazonSQSAsync.sendMessage(sendMessageRequest);
	}

	private SendMessageRequest createMessageRequest(SignUpEmailMessage message) {
		SendMessageRequest sendMessageRequest = new SendMessageRequest(queueUrl,MESSAGE_BODY);
		HashMap<String, MessageAttributeValue> attributeMap = new HashMap<>();
		attributeMap.put("email", new MessageAttributeValue()
			.withDataType("String")
			.withStringValue(message.getEmail()));
		attributeMap.put("code", new MessageAttributeValue()
			.withDataType("String")
			.withStringValue(message.getCode()));
		sendMessageRequest.setMessageAttributes(attributeMap);
		return sendMessageRequest;
	}
}

