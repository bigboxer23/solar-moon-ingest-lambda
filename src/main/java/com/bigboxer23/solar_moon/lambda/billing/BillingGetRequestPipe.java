package com.bigboxer23.solar_moon.lambda.billing;

import com.bigboxer23.solar_moon.lambda.MethodHandler;
import com.bigboxer23.solar_moon.lambda.RequestPipe;
import java.util.HashMap;
import java.util.Map;

/** */
public class BillingGetRequestPipe extends RequestPipe {
	private static final Map<String, MethodHandler> functions = new HashMap<>();

	static {
		functions.put("/billing/status", new CheckoutSessionStatus());
		functions.put("/billing/subscriptions", new GetSubscriptions());
	}

	@Override
	public Map<String, MethodHandler> getFunction() {
		return functions;
	}
}
