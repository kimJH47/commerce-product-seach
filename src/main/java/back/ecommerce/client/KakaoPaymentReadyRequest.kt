package back.ecommerce.client

import org.springframework.util.LinkedMultiValueMap

data class KakaoPaymentReadyRequest(
    var cid: String,
    val partnerOrderId: String,
    val partnerUserId: String,
    val itemName: String,
    val quantity: Int,
    val totalAmount: Long,
    val taxFreeAmount: Long,
    val approvalUrl: String,
    val cancelUrl: String,
    val failUrl: String,
) {

    fun toMap(): LinkedMultiValueMap<String, String> {
        val map = LinkedMultiValueMap<String, String>().apply {
            put("cid", mutableListOf(cid))
            put("partner_order_id", mutableListOf(partnerOrderId))
            put("partner_user_id", mutableListOf(partnerUserId))
            put("item_name", mutableListOf(itemName))
            put("quantity", mutableListOf(quantity.toString()))
            put("total_amount", mutableListOf(totalAmount.toString()))
            put("tax_free_amount", mutableListOf(taxFreeAmount.toString()))
            put("approval_url", mutableListOf(approvalUrl))
            put("cancel_url", mutableListOf(cancelUrl))
            put("fail_url", mutableListOf(failUrl))
        }
        return map
    }
}