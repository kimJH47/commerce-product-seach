package back.ecommerce.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;

public class SearchRequestLogger implements CustomLogger {

	private static final Logger SearchRequestLogger = LoggerFactory.getLogger(SearchRequestLogger.class);

	@Override
	public void log(Level level, String format, Object... arguments) {
		SearchRequestLogger.info(format,arguments);
	}
}
