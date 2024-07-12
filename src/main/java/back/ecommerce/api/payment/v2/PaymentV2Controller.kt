package back.ecommerce.api.payment.v2

import back.ecommerce.api.dto.Response
import back.ecommerce.api.payment.PaymentReadyResponse
import back.ecommerce.api.auth.resolver.annotation.UserEmail
import back.ecommerce.client.KakaoPaymentClient
import back.ecommerce.order.service.OrderService
import back.ecommerce.payment.dto.request.PaymentCancelRequest
import back.ecommerce.payment.dto.request.PaymentReadyRequest
import back.ecommerce.payment.service.PaymentService
import jakarta.validation.Valid
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/v2/api/payment")
class PaymentV2Controller(
    private val kakaoPaymentClient: KakaoPaymentClient,
    private val paymentService: PaymentService,
    private val orderService: OrderService,
) {

    @PostMapping("/ready")
    fun paymentReady(
        @UserEmail email: String,
        @Valid @RequestBody request: PaymentReadyRequest
    ): ResponseEntity<Response> {
        val createOrder = orderService.createOrder(email, request.totalPrice, request.orderProducts)
        val result = kakaoPaymentClient.ready(
            email,
            createOrder.orderCode,
            createOrder.totalPrice,
            createOrder.name,
            createOrder.quantity
        )

        paymentService.createReadyPayment(result.transactionId, result.cid, result.orderCode, email, request.totalPrice)
        return Response.createSuccessResponse(
            "결제준비가 완료 되었습니다.",
            PaymentReadyResponse(result.pcUrl, result.orderCode, result.createdAt)
        )
    }

    @GetMapping("/callback-approval/{orderCode}")
    fun approval(
        @PathVariable("orderCode") orderCode: String,
        @RequestParam("pg_token") token: String
    ): ResponseEntity<Any> {
        val payment = paymentService.findByOrderCode(orderCode)
        kakaoPaymentClient.approval(token, payment.transactionId, payment.orderCode, payment.userEmail)
        return ResponseEntity.status(HttpStatus.MOVED_PERMANENTLY)
            .header(HttpHeaders.LOCATION, "/")
            .build()
    }

    @PostMapping("/approval-cancel")
    fun approvalCancel(@RequestBody request: PaymentCancelRequest): ResponseEntity<Any> {
        val cancelPayment = paymentService.cancel(request.orderCode)
        kakaoPaymentClient.cancel(cancelPayment.transactionId, request.orderCode, cancelPayment.totalPrice)
        return ResponseEntity.status(HttpStatus.MOVED_PERMANENTLY)
            .header(HttpHeaders.LOCATION, "/")
            .build()
    }

    @GetMapping("/callback-failure/{orderCode}")
    fun fail(@PathVariable("orderCode") orderCode: String): ResponseEntity<Any> {
        paymentService.fail(orderCode)
        return ResponseEntity.status(HttpStatus.MOVED_PERMANENTLY)
            .header(HttpHeaders.LOCATION, "/")
            .build()
    }
}