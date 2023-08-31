package back.ecommerce.controller.common;

import java.util.EnumMap;

import org.slf4j.event.Level;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class GlobalLogger {

	private final EnumMap<Level, Logger> functionEnumMap = new EnumMap<>(Level.class);

	public GlobalLogger() {
		functionEnumMap.put(Level.INFO, log::info);
		functionEnumMap.put(Level.WARN, log::warn);
		functionEnumMap.put(Level.ERROR, log::error);
	}

	public void log(Level level, String format, Object... arguments) {
		Logger logger = functionEnumMap.get(level);
		logger.log(format, arguments);
	}

	@FunctionalInterface
	interface Logger {
		void log(String format, Object... arguments);
	}
}
