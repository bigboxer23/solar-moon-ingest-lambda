package com.bigboxer23.solar_moon.lambda;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.JoranException;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import com.bigboxer23.solar_moon.CustomerComponent;
import com.squareup.moshi.Moshi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** */
public abstract class AbstractRequestStreamHandler implements RequestStreamHandler {
	protected static final Moshi moshi = new Moshi.Builder().build();

	protected static final Logger logger = LoggerFactory.getLogger(UploadFunction.class);
	protected static final CustomerComponent customerComponent = new CustomerComponent();

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
		logger.warn("Static Initialized");
	}

	public static void setupLogging() {
		LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
		loggerContext.reset();
		JoranConfigurator config = new JoranConfigurator();
		config.setContext(loggerContext);
		try {
			config.doConfigure(UploadFunction.class.getResourceAsStream("/logback.xml"));
		} catch (JoranException e) {
			logger.error("Cannot initialize logger context ", e);
		}
		logger.warn("Logging Initialized");
	}
}
