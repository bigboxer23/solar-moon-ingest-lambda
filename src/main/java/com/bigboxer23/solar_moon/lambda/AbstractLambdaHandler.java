package com.bigboxer23.solar_moon.lambda;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.JoranException;
import com.bigboxer23.solar_moon.IComponentRegistry;
import com.bigboxer23.solar_moon.lambda.ingest.UploadFunction;
import com.bigboxer23.solar_moon.logging.OpenSearchAppender;
import com.bigboxer23.solar_moon.web.TransactionUtil;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** */
public class AbstractLambdaHandler implements IComponentRegistry {
	protected static final Logger logger = LoggerFactory.getLogger(AbstractRequestStreamHandler.class);

	static {
		setupLogging();
		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
			logger.warn("[runtime] Cleaning up");
			try {
				Thread.sleep(1000);
			} catch (Exception e) {
			}
			System.exit(0);
		}));
		logger.debug("Static Initialized");
	}

	public static void setupLogging() {
		logger.debug("setting up logging");
		try {
			LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
			loggerContext.reset();
			JoranConfigurator config = new JoranConfigurator();
			config.setContext(loggerContext);
			try {
				config.doConfigure(UploadFunction.class.getResourceAsStream("/logback.xml"));
			} catch (JoranException e) {
				logger.error("Cannot initialize logger context ", e);
			}
		} catch (Exception e) {
			logger.warn("logging exception", e);
		}
		logger.debug("Logging Initialized");
	}

	@SneakyThrows
	protected void after() {
		TransactionUtil.clear();
		OpenSearchAppender.waitForPendingData();
	}
}
