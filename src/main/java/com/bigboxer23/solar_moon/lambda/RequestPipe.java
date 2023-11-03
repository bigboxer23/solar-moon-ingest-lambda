package com.bigboxer23.solar_moon.lambda;

import com.bigboxer23.solar_moon.lambda.data.LambdaRequest;
import com.bigboxer23.solar_moon.lambda.data.LambdaResponse;
import java.io.IOException;
import java.util.Map;

/** */
public abstract class RequestPipe extends MethodHandler {
	@Override
	public LambdaResponse handleLambdaRequest(LambdaRequest request) throws IOException {
		if (!getFunction().containsKey(request.getPath())) {
			logger.warn("Not able to service " + request.getPath());
			return new LambdaResponse(BAD_REQUEST, null, APPLICATION_JSON_VALUE);
		}
		return getFunction().get(request.getPath()).handleLambdaRequest(request);
	}

	@Override
	public boolean isPricingRedirectEnabled(LambdaRequest request) {
		return !getFunction().containsKey(request.getPath())
				|| getFunction().get(request.getPath()).isPricingRedirectEnabled(request);
	}

	public abstract Map<String, MethodHandler> getFunction();
}
