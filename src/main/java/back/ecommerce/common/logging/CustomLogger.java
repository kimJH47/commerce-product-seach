package back.ecommerce.common.logging;

import org.slf4j.event.Level;

public interface CustomLogger {

	void log(Level level, String format, Object... arguments);

	@FunctionalInterface
	interface LoggingFunction {
		void log(String format, Object... arguments);
	}
}
