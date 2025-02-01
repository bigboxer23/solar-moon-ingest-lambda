package com.bigboxer23.solar_moon.lambda.billing;

import com.bigboxer23.payments.StripeWebhookComponent;
import com.bigboxer23.solar_moon.lambda.MethodHandler;
import com.bigboxer23.solar_moon.lambda.data.LambdaRequest;
import com.bigboxer23.solar_moon.lambda.data.LambdaResponse;
import com.stripe.exception.StripeException;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;

/** */
@Slf4j
public class BillingWebhook extends MethodHandler {
	private static final StripeWebhookComponent component = new StripeWebhookComponent();

	@Override
	public LambdaResponse handleLambdaRequest(LambdaRequest request) throws IOException {
		try {
			component.webhook(request.getHeaders().getStripeSignature(), request.getBody());
		} catch (StripeException e) {
			log.warn("handleLambdaRequest", e);
			return new LambdaResponse(BAD_REQUEST, "Subscription not updated.");
		}
		return new LambdaResponse(OK, "Subscription updated.");
	}

	@Override
	public boolean isPricingRedirectEnabled(LambdaRequest request) {
		return false;
	}
}
