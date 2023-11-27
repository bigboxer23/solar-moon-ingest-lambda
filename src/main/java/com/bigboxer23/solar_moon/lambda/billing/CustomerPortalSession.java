package com.bigboxer23.solar_moon.lambda.billing;

import com.bigboxer23.payments.StripeBillingPortalComponent;
import com.bigboxer23.solar_moon.lambda.MethodHandler;
import com.bigboxer23.solar_moon.lambda.data.LambdaRequest;
import com.bigboxer23.solar_moon.lambda.data.LambdaResponse;
import com.stripe.exception.StripeException;
import java.io.IOException;

/** */
public class CustomerPortalSession extends MethodHandler {
	private static final StripeBillingPortalComponent component = new StripeBillingPortalComponent();

	@Override
	public LambdaResponse handleLambdaRequest(LambdaRequest request) throws IOException {
		try {
			return new LambdaResponse(
					OK,
					component.createCustomerPortalSession(customerComponent
							.findCustomerByCustomerId(getCustomerIdFromRequest(request))
							.orElse(null)));
		} catch (StripeException e) {
			logger.warn("CustomerPortalSession", e);
			return new LambdaResponse(BAD_REQUEST, "error creating portal session");
		}
	}
}
