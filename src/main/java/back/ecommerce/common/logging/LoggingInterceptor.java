package back.ecommerce.common.logging;

import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.MDC;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

public class LoggingInterceptor implements HandlerInterceptor {

	public static final String REQUEST_CONTROLLER_MDC_KEY = "handler";

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws
		Exception {
		if (handler instanceof HandlerMethod) {
			HandlerMethod handlerMethod = (HandlerMethod)handler;
			String handlerName = handlerMethod.getBeanType().getSimpleName();
			String methodName = handlerMethod.getMethod().getName();
			String controllerInfo = handlerName + "." + methodName;
			MDC.put(REQUEST_CONTROLLER_MDC_KEY, controllerInfo);
			MDC.put("requestId", UUID.randomUUID().toString()); //nginx proxy request id 로 대체가능
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
