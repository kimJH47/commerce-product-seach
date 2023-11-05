package back.ecommerce.client;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ApprovalCancelAmount {
	private Integer total;
	@JsonProperty("tax_free")
	private Integer taxFree;
	private Integer vat;
	private Integer point;
	private Integer discount;
	@JsonProperty("green_deposit")
	private Integer greenDeposit;
}
