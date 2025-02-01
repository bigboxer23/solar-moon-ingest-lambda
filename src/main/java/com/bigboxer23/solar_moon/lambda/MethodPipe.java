package com.bigboxer23.solar_moon.lambda;

import com.bigboxer23.solar_moon.lambda.data.LambdaRequest;
import com.bigboxer23.solar_moon.lambda.data.LambdaResponse;
import com.bigboxer23.solar_moon.web.TransactionUtil;
import java.io.IOException;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;

/** */
@Slf4j
public abstract class MethodPipe extends AbstractRequestStreamHandler {
	@Override
	public LambdaResponse handleLambdaRequest(LambdaRequest request) throws IOException {
		if (!getFunction().containsKey(request.getHttpMethod())) {
			log.warn("Not able to service " + request.getHttpMethod());
			return new LambdaResponse(BAD_REQUEST, null, APPLICATION_JSON_VALUE);
		}
		TransactionUtil.updateServiceCalled(
				getFunction().get(request.getHttpMethod()).getClass().getSimpleName());
		return getFunction().get(request.getHttpMethod()).handleLambdaRequest(request);
	}

	protected boolean isPricingRedirectEnabled(LambdaRequest request) {
		return !getFunction().containsKey(request.getHttpMethod())
				|| getFunction().get(request.getHttpMethod()).isPricingRedirectEnabled(request);
	}

	public abstract Map<String, MethodHandler> getFunction();
}
