package com.bigboxer23.solar_moon.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import com.bigboxer23.solar_moon.lambda.data.LambdaRequest;
import com.bigboxer23.solar_moon.lambda.data.LambdaResponse;
import com.bigboxer23.solar_moon.web.AuthenticationUtils;
import com.bigboxer23.solar_moon.web.Transaction;
import com.bigboxer23.solar_moon.web.TransactionUtil;
import com.bigboxer23.utils.command.VoidCommand;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;

/** */
@Slf4j
public abstract class AbstractRequestStreamHandler extends AbstractLambdaHandler
		implements RequestStreamHandler, MediaTypes, HttpStatus {
	@Transaction
	public abstract LambdaResponse handleLambdaRequest(LambdaRequest request) throws IOException;

	@Override
	public void handleRequest(InputStream inputStream, OutputStream outputStream, Context context) throws IOException {
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
				OutputStreamWriter writer = new OutputStreamWriter(outputStream, StandardCharsets.UTF_8)) {
			String rawRequest = IOUtils.toString(reader);
			log.debug("request:\n" + rawRequest);
			Optional.ofNullable(moshi.adapter(LambdaRequest.class).fromJson(rawRequest))
					.ifPresent(request -> {
						TransactionUtil.newTransaction(request);
						log.debug("request: " + request);
						try {
							if (isRedirectingToPricing(request, writer)) {
								after();
								return;
							}
							writer.write(moshi.adapter(LambdaResponse.class).toJson(handleLambdaRequest(request)));
						} catch (Exception e) {
							log.warn("handleRequest:", e);
							try {
								writer.write(moshi.adapter(LambdaResponse.class)
										.toJson(new LambdaResponse(BAD_REQUEST, null, APPLICATION_JSON_VALUE)));
							} catch (IOException theE) {

							}
						}
					});
		} catch (Exception e) {
			log.warn("handleRequest", e);
		}
		after();
	}

	private boolean isRedirectingToPricing(LambdaRequest request, OutputStreamWriter writer) throws IOException {
		if (isPricingRedirectEnabled(request)
				&& subscriptionComponent.getSubscriptionDevices(AuthenticationUtils.getCustomerIdFromRequest(request))
						<= 0) {
			log.warn("No subscription or trial exists for "
					+ AuthenticationUtils.getCustomerIdFromRequest(request)
					+ ", redirecting to pricing.");
			writer.write(moshi.adapter(LambdaResponse.class)
					.toJson(new LambdaResponse(FORBIDDEN, "No subscription is active", APPLICATION_JSON_VALUE)));
			return true;
		}
		return false;
	}

	protected boolean isPricingRedirectEnabled(LambdaRequest request) {
		return true;
	}

	protected void safeHandleRequest(VoidCommand command) {
		TransactionUtil.updateServiceCalled(getClass().getSimpleName());
		try {
			command.execute();
		} catch (Exception e) {
			log.error("safeHandleRequest:", e);
		}
		after();
	}
}
