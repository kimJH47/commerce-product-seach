package back.ecommerce.common.logging.aop.aspect;

import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.MDC;
import org.slf4j.event.Level;
import org.springframework.util.StringUtils;

import back.ecommerce.common.logging.CustomLogger;
import back.ecommerce.common.logging.GlobalLogger;
import back.ecommerce.dto.request.product.ProductSearchConditionRequest;
import back.ecommerce.exception.CustomException;
import lombok.extern.slf4j.Slf4j;

@Aspect
@Slf4j
public class LoggingAspect {

	private static final String INFO_LOG_FORMAT = "[INFO] user request api";
	private static final String WARN_LOG_FORMAT = "[WARN] reason : {} | message : {}";
	private static final String DEFAULT_ANONYMOUS_USER = "NONE";
	private final CustomLogger globalLogger;
	private final CustomLogger searchRequestLogger;

	public LoggingAspect(CustomLogger searchRequestLogger, GlobalLogger globalLogger) {
		this.globalLogger = globalLogger;
		this.searchRequestLogger = searchRequestLogger;

	}

	@AfterReturning(value = "@annotation(back.ecommerce.common.logging.aop.annotation.Logging) && args(request)")
	public void requestLogging(ProductSearchConditionRequest request) {
		putRequestToMDC(request);
		searchRequestLogger.log(Level.INFO, INFO_LOG_FORMAT);
	}

	private void putRequestToMDC(ProductSearchConditionRequest request) {
		String user_email = MDC.get("userEmail");
		if (!StringUtils.hasText(user_email)) {
			MDC.put("userEmail", DEFAULT_ANONYMOUS_USER);
		}
		MDC.put("category", request.getCategory().toString());
		MDC.put("condition", request.getParameters().toString());
	}

	@Before(value = "@annotation(org.springframework.web.bind.annotation.ExceptionHandler) && args(e)")
	public void exceptionLogging(CustomException e) {
		globalLogger.log(Level.WARN, WARN_LOG_FORMAT, e.getReasonField(), e.getDescription());
	}
}
