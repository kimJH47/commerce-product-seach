package back.ecommerce.client;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class KakaoPaymentApprovalResult {
	private  String aid;
	private  String tid;
	@JsonProperty("partner_order_id")
	private  String orderCode;
	@JsonProperty("partner_user_id")
	private  String userEmail;
	@JsonProperty("payment_method_type")
	private  PaymentType paymentType;
	private  Amount amount;
	@JsonProperty("card_info")
	private  CardInfo cardInfo;
	private  int quantity;
	@JsonProperty("created_at")
	private  LocalDateTime createdAt;
	@JsonProperty("approved_at")
	private  LocalDateTime approvedAt;
	private  String payload;
}
