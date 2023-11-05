package back.ecommerce.client;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class KakaoPaymentCancelResult {
	private String aid;
	private String tid;
	private String cid;
	private KakaoPaymentStatus status;
	@JsonProperty("partner_order_id")
	private String orderCode;
	@JsonProperty("partner_user_id")
	private String userEmail;
	@JsonProperty("payment_method_type")
	private PaymentType paymentType;
	private Amount amount;
	@JsonProperty("approved_cancel_amount")
	private ApprovalCancelAmount approvalCancelAmount;
	@JsonProperty("item_code")
	private String itemCode;
	@JsonProperty("item_name")
	private String itemName;
	private Integer quantity;
	@JsonProperty("created_at")
	private LocalDateTime createdAt;
	@JsonProperty("approved_at")
	private LocalDateTime approvedAt;
	@JsonProperty("canceled_at")
	private LocalDateTime canceledAt;
	private String payload;

}
