package com.bigboxer23.solar_moon.lambda;

import com.bigboxer23.solar_moon.lambda.data.LambdaRequest;
import com.bigboxer23.solar_moon.lambda.data.LambdaResponse;
import java.io.IOException;
import java.util.Map;

/** */
public abstract class MethodPipe extends AbstractRequestStreamHandler {
	@Override
	public LambdaResponse handleLambdaRequest(LambdaRequest request) throws IOException {
		if (!getFunction().containsKey(request.getHttpMethod())) {
			logger.warn("Not able to service " + request.getHttpMethod());
			return new LambdaResponse(BAD_REQUEST, null, APPLICATION_JSON_VALUE);
		}
		return getFunction().get(request.getHttpMethod()).handleLambdaRequest(request);
	}

	public abstract Map<String, MethodHandler> getFunction();
}
