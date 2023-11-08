package com.bigboxer23.solar_moon.lambda.device;

import com.bigboxer23.solar_moon.lambda.MethodHandler;
import com.bigboxer23.solar_moon.lambda.data.LambdaRequest;
import com.bigboxer23.solar_moon.lambda.data.LambdaResponse;
import java.io.IOException;

/** */
public class DeviceDelete extends MethodHandler {
	@Override
	public LambdaResponse handleLambdaRequest(LambdaRequest request) throws IOException {
		if (request.getPath().equals("/devices/") || request.getPath().equals("/devices")) {
			logger.warn("DeviceDelete: no device id included.");
			return new LambdaResponse(BAD_REQUEST, "No device id included", APPLICATION_JSON_VALUE);
		}
		deviceComponent.deleteDevice(DeviceGet.idFromPath(request.getPath()), getCustomerIdFromRequest(request));
		return new LambdaResponse(OK, "Deleted device", APPLICATION_JSON_VALUE);
	}
}
