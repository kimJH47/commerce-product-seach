package back.ecommerce.aop;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.event.Level;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import back.ecommerce.controller.common.CustomLogger;

@Aspect
public class LoggingAspect {

	private static final String LOG_FORMAT = "[INFO] request: {} {} | userEmail : {} | category: {} | searchCondition:{}";
	private static final String DEFAULT_ANONYMOUS_NAME = "NONE";
	private final CustomLogger logger;
	public LoggingAspect(CustomLogger customLogger) {
		this.logger = customLogger;
	}

	@AfterReturning(value = "@annotation(back.ecommerce.aop.annotation.Logging) && args(category,searchCondition)", argNames = "category,searchCondition")
		public void requestLogging(String category, Map<String,String> searchCondition){
		ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes)RequestContextHolder.currentRequestAttributes();
		HttpServletRequest request = servletRequestAttributes.getRequest();
		RequestContextHolder.getRequestAttributes();
		logger.log(Level.INFO, LOG_FORMAT, request.getRequestURI(), request.getMethod(),
			DEFAULT_ANONYMOUS_NAME, category, searchCondition.entrySet());
	}
}
