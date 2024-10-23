package back.ecommerce.common.logging;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.slf4j.MDC;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

public class LoggingInterceptor implements HandlerInterceptor {

	public static final String REQUEST_CONTROLLER_MDC_KEY = "handler";
	public static final String REQUEST_ID = "X-REQUEST-ID";

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws
		Exception {
		if (handler instanceof HandlerMethod) {
			HandlerMethod handlerMethod = (HandlerMethod)handler;
			String handlerName = handlerMethod.getBeanType().getSimpleName();
			String methodName = handlerMethod.getMethod().getName();
			String controllerInfo = handlerName + "." + methodName;
			MDC.put(REQUEST_CONTROLLER_MDC_KEY, controllerInfo);
			MDC.put("requestId", request.getHeader(REQUEST_ID)); //nginx proxy request id 로 대체가능
			MDC.put("url", request.getRequestURI());
			MDC.put("httpMethod", request.getMethod());
		}
		return true;
	}

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler,
		Exception ex) throws Exception {
		MDC.clear();
	}
}
