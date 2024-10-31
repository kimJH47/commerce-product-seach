package back.ecommerce.common.logging

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.MDC
import org.slf4j.event.Level
import org.springframework.web.method.HandlerMethod
import org.springframework.web.servlet.HandlerInterceptor

class LoggingInterceptor(
    private val globalLogger: GlobalLogger
) : HandlerInterceptor {

    @Throws(Exception::class)
    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {
        if (handler is HandlerMethod) {
            val handlerName = handler.beanType.simpleName
            val methodName = handler.method.name
            val controllerInfo = "$handlerName.$methodName"
            MDC.put(REQUEST_CONTROLLER_MDC_KEY, controllerInfo)
            MDC.put("requestId", request.getHeader(REQUEST_ID)) //nginx proxy request id 로 대체가능
            MDC.put("url", request.requestURI)
            MDC.put("httpMethod", request.method)

            globalLogger.log(Level.INFO, "API 요청이 발생했습니다.")
        }
        return true
    }

    @Throws(Exception::class)
    override fun afterCompletion(
        request: HttpServletRequest, response: HttpServletResponse, handler: Any, ex: Exception?
    ) {
        MDC.clear()
    }

    companion object {
        const val REQUEST_CONTROLLER_MDC_KEY: String = "handler"
        const val REQUEST_ID: String = "X-REQUEST-ID"
    }
}
