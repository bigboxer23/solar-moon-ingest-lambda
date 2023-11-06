package com.bigboxer23.solar_moon.lambda;

import com.bigboxer23.solar_moon.lambda.billing.BillingEndpoint;
import com.bigboxer23.solar_moon.lambda.customer.CustomerEndpoint;
import com.bigboxer23.solar_moon.lambda.data.LambdaRequest;
import com.bigboxer23.solar_moon.lambda.data.LambdaResponse;
import com.bigboxer23.solar_moon.lambda.device.DeviceEndpoint;
import com.bigboxer23.solar_moon.lambda.search.SearchEndpoint;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/** */
public class BaseRequestPipe extends AbstractRequestStreamHandler {
	private static final Map<String, MethodPipe> functions = new HashMap<>();

	static {
		functions.put("/billing", new BillingEndpoint());
		functions.put("/customer", new CustomerEndpoint());
		functions.put("/devices", new DeviceEndpoint());
		functions.put("/search", new SearchEndpoint());
		// functions.put("/billing/checkout", new UploadFunction());
	}

	public LambdaResponse handleLambdaRequest(LambdaRequest request) throws IOException {
		MethodPipe handler = findMatchingPath(request.getPath());
		if (handler == null) {
			logger.warn("Not able to service " + request.getPath());
			return new LambdaResponse(BAD_REQUEST, null, APPLICATION_JSON_VALUE);
		}
		return handler.handleLambdaRequest(request);
	}

	@Override
	public boolean isPricingRedirectEnabled(LambdaRequest request) {
		MethodPipe handler = findMatchingPath(request.getPath());
		return handler == null || handler.isPricingRedirectEnabled(request);
	}

	private MethodPipe findMatchingPath(String path) {
		return functions.keySet().stream()
				.filter(path::startsWith)
				.findAny()
				.map(functions::get)
				.orElse(null);
	}
}
