package com.bigboxer23.solar_moon.lambda.billing;

import com.bigboxer23.payments.CheckoutPrice;
import com.bigboxer23.payments.StripeCheckoutComponent;
import com.bigboxer23.solar_moon.lambda.MethodHandler;
import com.bigboxer23.solar_moon.lambda.data.LambdaRequest;
import com.bigboxer23.solar_moon.lambda.data.LambdaResponse;
import com.stripe.exception.StripeException;
import java.io.IOException;
import java.util.Map;

/** */
public class CreateCheckoutSession extends MethodHandler {
	private static final StripeCheckoutComponent checkoutComponent = new StripeCheckoutComponent();

	@Override
	public LambdaResponse handleLambdaRequest(LambdaRequest request) throws IOException {
		try {
			return new LambdaResponse(
					OK,
					moshi.adapter(Map.class)
							.toJson(checkoutComponent.createCheckoutSession(
									getCustomerFromRequest(request).orElse(null),
									moshi.adapter(CheckoutPrice.class).fromJson(request.getBody()))),
					APPLICATION_JSON_VALUE);
		} catch (StripeException e) {
			logger.warn("CheckoutSessionStatus", e);
			return new LambdaResponse(BAD_REQUEST, "error creating checkout session");
		}
	}

	@Override
	public boolean isPricingRedirectEnabled(LambdaRequest request) {
		return false;
	}
}
