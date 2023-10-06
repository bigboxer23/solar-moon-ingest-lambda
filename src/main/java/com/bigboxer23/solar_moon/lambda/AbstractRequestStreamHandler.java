package com.bigboxer23.solar_moon.lambda;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.JoranException;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import com.bigboxer23.solar_moon.lambda.data.LambdaRequest;
import com.bigboxer23.solar_moon.lambda.data.LambdaResponse;
import com.bigboxer23.solar_moon.lambda.ingest.UploadFunction;
import com.bigboxer23.solar_moon.web.Transaction;
import com.bigboxer23.solar_moon.web.TransactionUtil;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import lombok.SneakyThrows;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** */
public abstract class AbstractRequestStreamHandler
		implements RequestStreamHandler, MediaTypes, HttpStatus, IComponentRegistry {
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
		LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
		loggerContext.reset();
		JoranConfigurator config = new JoranConfigurator();
		config.setContext(loggerContext);
		try {
			config.doConfigure(UploadFunction.class.getResourceAsStream("/logback.xml"));
		} catch (JoranException e) {
			logger.error("Cannot initialize logger context ", e);
		}
		logger.debug("Logging Initialized");
	}

	@Transaction
	public abstract LambdaResponse handleLambdaRequest(LambdaRequest request) throws IOException;

	@SneakyThrows
	@Override
	public void handleRequest(InputStream inputStream, OutputStream outputStream, Context context) throws IOException {
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
				OutputStreamWriter writer = new OutputStreamWriter(outputStream, StandardCharsets.UTF_8)) {
			String rawRequest = IOUtils.toString(reader);
			logger.debug("request:" + rawRequest);
			Optional.ofNullable(moshi.adapter(LambdaRequest.class).fromJson(rawRequest))
					.ifPresent(request -> {
						TransactionUtil.newTransaction(request);
						logger.debug("request: " + request);
						try {
							writer.write(moshi.adapter(LambdaResponse.class).toJson(handleLambdaRequest(request)));
						} catch (IOException e) {
							logger.warn("handleRequest:", e);
						}
					});
		}
		TransactionUtil.clear();
		Thread.sleep(750);
	}
}
