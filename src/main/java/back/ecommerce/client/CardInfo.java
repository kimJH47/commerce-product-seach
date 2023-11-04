package back.ecommerce.client;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class CardInfo {
	@JsonProperty("purchase_corp")
	private String purchaseCorp;
	@JsonProperty("purchase_corp_code")
	private String purchaseCorpCode;
	@JsonProperty("issuer_corp")
	private String issuerCorp;
	@JsonProperty("issuer_corp_code")
	private String issuerCorpCode;
	@JsonProperty("kakaopay_purchase_corp")
	private String kakaopayPurchaseCorp;
	@JsonProperty("kakaopay_purchase_corp_code")
	private String kakaoPurchaseCorpCode;
	@JsonProperty("kakaopay_issuer_corp")
	private String kakaopayIssuerCorp;
	@JsonProperty("kakaopay_issuer_corp_code")
	private String kakaopayIssuerCorpCode;
	private String bin;
	@JsonProperty("card_type")
	private String cardType;
	@JsonProperty("install_month")
	private String installMonth;
	@JsonProperty("approved_id")
	private String approvedId;
	@JsonProperty("card_mid")
	private String cardMid;
	@JsonProperty("interest_free_install")
	private String interestFreeInstall;
	@JsonProperty("card_item_code")
	private String cardItemCode;
}
