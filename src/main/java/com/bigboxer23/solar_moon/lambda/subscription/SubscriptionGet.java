package com.bigboxer23.solar_moon.lambda.subscription;

import com.bigboxer23.solar_moon.lambda.MethodHandler;
import com.bigboxer23.solar_moon.lambda.data.LambdaRequest;
import com.bigboxer23.solar_moon.lambda.data.LambdaResponse;
import java.io.IOException;

/** */
public class SubscriptionGet extends MethodHandler {
	@Override
	public LambdaResponse handleLambdaRequest(LambdaRequest request) throws IOException {
		return new LambdaResponse(
				OK,
				subscriptionComponent.getSubscriptionPacks(getCustomerIdFromRequest(request)) + "",
				APPLICATION_JSON_VALUE);
	}
}
