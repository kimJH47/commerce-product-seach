package back.ecommerce.api.payment;

import org.springframework.util.LinkedMultiValueMap;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class KakaoPaymentReadyRequest {
	private String cid;
	private String partner_order_id;
	private String partner_user_id;
	private String item_name;
	private Integer quantity;
	private Long total_amount;
	private Long tax_free_amount;
	private String approval_url;
	private String cancel_url;
	private String fail_url;

	public LinkedMultiValueMap<String, String> toMap() {
		LinkedMultiValueMap<String, String> map = new LinkedMultiValueMap<>();
		map.add("cid", cid);
		map.add("partner_order_id", partner_order_id);
		map.add("partner_user_id", partner_user_id);
		map.add("item_name", item_name);
		map.add("quantity", quantity.toString());
		map.add("total_amount", total_amount.toString());
		map.add("tax_free_amount", tax_free_amount.toString());
		map.add("approval_url", approval_url);
		map.add("cancel_url", cancel_url);
		map.add("fail_url", fail_url);
		return map;
	}
}
