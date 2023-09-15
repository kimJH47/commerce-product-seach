package back.ecommerce.infrastructure.aws.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.sqs.AmazonSQSAsync;
import com.amazonaws.services.sqs.AmazonSQSAsyncClientBuilder;

import back.ecommerce.infrastructure.aws.SQSEmailSender;
import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class AwsConfig {
	@Value("${cloud.aws.credentials.access-key}")
	private String accessKey;

	@Value("${cloud.aws.credentials.secret-key}")
	private String secretKey;

	@Value("${cloud.aws.region.static}")
	private String region;

	@Bean
	@Primary
	public AmazonSQSAsync amazonSQSAws() {
		BasicAWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey);
		return AmazonSQSAsyncClientBuilder.standard()
			.withRegion(region)
			.withCredentials(new AWSStaticCredentialsProvider(credentials))
			.build();
	}

	@Bean
	public SQSEmailSender sqsEmailSender(AmazonSQSAsync amazonSQSAsync, @Value("{cloud.aws.url") String url) {
		return new SQSEmailSender(amazonSQSAsync, url);
	}
}
