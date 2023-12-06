package com.bigboxer23.solar_moon.lambda.mapping;

import com.bigboxer23.solar_moon.lambda.MethodHandler;
import com.bigboxer23.solar_moon.lambda.data.LambdaRequest;
import com.bigboxer23.solar_moon.lambda.data.LambdaResponse;
import com.bigboxer23.solar_moon.lambda.device.DeviceGet;
import java.io.IOException;

/** */
public class MappingDelete extends MethodHandler {
	@Override
	public LambdaResponse handleLambdaRequest(LambdaRequest request) throws IOException {
		mappingComponent.deleteMapping(getCustomerIdFromRequest(request), DeviceGet.idFromPath(request.getPath()));
		return new LambdaResponse(OK, "Deleted mapping", APPLICATION_JSON_VALUE);
	}
}
