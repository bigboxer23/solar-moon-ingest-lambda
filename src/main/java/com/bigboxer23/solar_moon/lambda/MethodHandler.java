package com.bigboxer23.solar_moon.lambda;

import com.bigboxer23.solar_moon.lambda.data.LambdaRequest;
import com.bigboxer23.solar_moon.lambda.data.LambdaResponse;
import com.bigboxer23.solar_moon.web.AuthenticationUtils;
import com.bigboxer23.solar_moon.web.Transaction;
import java.io.IOException;

/** */
public abstract class MethodHandler implements MediaTypes, HttpStatus, IComponentRegistry {
	@Transaction
	public abstract LambdaResponse handleLambdaRequest(LambdaRequest request) throws IOException;

	public String getCustomerIdFromRequest(LambdaRequest request) {
		return AuthenticationUtils.getCustomerIdFromRequest(request);
	}

	public boolean isPricingRedirectEnabled(LambdaRequest request) {
		return true;
	}
}
