package com.bigboxer23.solar_moon.lambda.mapping;

import com.bigboxer23.solar_moon.lambda.MethodHandler;
import com.bigboxer23.solar_moon.lambda.data.LambdaRequest;
import com.bigboxer23.solar_moon.lambda.data.LambdaResponse;
import com.bigboxer23.solar_moon.mapping.AttributeMap;
import java.io.IOException;
import java.util.Optional;

/** */
public class MappingDelete extends MethodHandler {
	@Override
	public LambdaResponse handleLambdaRequest(LambdaRequest request) throws IOException {
		return Optional.ofNullable(moshi.adapter(AttributeMap.class).fromJson(request.getBody()))
				.map(map -> {
					mappingComponent.deleteMapping(getCustomerIdFromRequest(request), map.getMappingName());
					return new LambdaResponse(OK, "Deleted mapping", APPLICATION_JSON_VALUE);
				})
				.orElseGet(() -> new LambdaResponse(BAD_REQUEST, "Error deleting new mapping", APPLICATION_JSON_VALUE));
	}
}
