package back.ecommerce.aop;

import javax.servlet.http.HttpServletRequest;

import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.event.Level;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import back.ecommerce.controller.common.CustomLogger;
import back.ecommerce.dto.request.product.ProductSearchConditionRequest;

@Aspect
public class LoggingAspect {

	private static final String LOG_FORMAT = "[INFO] request: {} {} | userEmail : {} | category: {} | searchCondition:{}";
	private static final String DEFAULT_ANONYMOUS_NAME = "NONE";
	private final CustomLogger logger;

	public LoggingAspect(CustomLogger customLogger) {
		this.logger = customLogger;
	}

	@AfterReturning(value = "@annotation(back.ecommerce.aop.annotation.Logging) && args(request)")
	public void requestLogging(ProductSearchConditionRequest request) {
		ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes)RequestContextHolder.currentRequestAttributes();
		HttpServletRequest servletRequest = servletRequestAttributes.getRequest();
		RequestContextHolder.getRequestAttributes();
		logger.log(Level.INFO, LOG_FORMAT, servletRequest.getRequestURI(), servletRequest.getMethod(),
			DEFAULT_ANONYMOUS_NAME, request.getCategory(), request.getParameters().entrySet());
	}
}
