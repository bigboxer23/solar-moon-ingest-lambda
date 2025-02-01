package com.bigboxer23.solar_moon.lambda;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.JoranException;
import com.bigboxer23.solar_moon.IComponentRegistry;
import com.bigboxer23.solar_moon.lambda.ingest.UploadFunction;
import com.bigboxer23.solar_moon.logging.OpenSearchAppender;
import com.bigboxer23.solar_moon.web.TransactionUtil;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.LoggerFactory;

/** */
@Slf4j
public class AbstractLambdaHandler implements IComponentRegistry {
	static {
		setupLogging();
		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
			log.warn("[runtime] Cleaning up");
			try {
				Thread.sleep(1000);
			} catch (Exception e) {
			}
			System.exit(0);
		}));
		log.debug("Static Initialized");
	}

	public static void setupLogging() {
		log.debug("setting up logging");
		try {
			LoggerContext logContext = (LoggerContext) LoggerFactory.getILoggerFactory();
			logContext.reset();
			JoranConfigurator config = new JoranConfigurator();
			config.setContext(logContext);
			try {
				config.doConfigure(UploadFunction.class.getResourceAsStream("/logback.xml"));
			} catch (JoranException e) {
				log.error("Cannot initialize log context ", e);
			}
		} catch (Exception e) {
			log.warn("logging exception", e);
		}
		log.debug("Logging Initialized");
	}

	@SneakyThrows
	protected void after() {
		TransactionUtil.clear();
		OpenSearchAppender.waitForPendingData();
	}
}
