package com.bigboxer23.solar_moon.lambda.billing;

import com.bigboxer23.solar_moon.lambda.MethodHandler;
import com.bigboxer23.solar_moon.lambda.RequestPipe;
import java.util.HashMap;
import java.util.Map;

/** */
public class BillingPostRequestPipe extends RequestPipe {
	private static final Map<String, MethodHandler> functions = new HashMap<>();

	static {
		functions.put("/billing-event", new BillingWebhook());
		functions.put("/billing/portal", new CustomerPortalSession());
		functions.put("/billing/checkout", new CreateCheckoutSession());
	}

	@Override
	public Map<String, MethodHandler> getFunction() {
		return functions;
	}
}
