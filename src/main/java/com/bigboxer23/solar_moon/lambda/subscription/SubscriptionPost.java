package com.bigboxer23.solar_moon.lambda.subscription;

import com.bigboxer23.solar_moon.data.Subscription;
import com.bigboxer23.solar_moon.lambda.MethodHandler;
import com.bigboxer23.solar_moon.lambda.data.LambdaRequest;
import com.bigboxer23.solar_moon.lambda.data.LambdaResponse;
import com.bigboxer23.solar_moon.subscription.SubscriptionComponent;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;

/** */
@Slf4j
public class SubscriptionPost extends MethodHandler {
	@Override
	public LambdaResponse handleLambdaRequest(LambdaRequest request) throws IOException {
		String customerId = getCustomerIdFromRequest(request);
		if (!subscriptionComponent.isTrialValid(customerId)) {
			log.warn("cannot renew trial, invalid");
			return new LambdaResponse(BAD_REQUEST, "cannot renew trial, invalid");
		}
		return new LambdaResponse(
				OK,
				moshi.adapter(Subscription.class)
						.toJson(subscriptionComponent.updateSubscription(customerId, SubscriptionComponent.TRIAL_MODE)),
				APPLICATION_JSON_VALUE);
	}

	@Override
	public boolean isPricingRedirectEnabled(LambdaRequest request) {
		return false;
	}
}
