package com.bigboxer23.solar_moon.lambda.mapping;

import com.bigboxer23.solar_moon.lambda.MethodHandler;
import com.bigboxer23.solar_moon.lambda.data.LambdaRequest;
import com.bigboxer23.solar_moon.lambda.data.LambdaResponse;
import com.bigboxer23.solar_moon.mapping.AttributeMap;
import java.io.IOException;
import java.util.Optional;

/** */
public class MappingAdd extends MethodHandler {
	@Override
	public LambdaResponse handleLambdaRequest(LambdaRequest request) throws IOException {
		return Optional.ofNullable(moshi.adapter(AttributeMap.class).fromJson(request.getBody()))
				.map(map -> {
					Optional<AttributeMap> mapping = mappingComponent.addMapping(
							getCustomerIdFromRequest(request), map.getAttribute(), map.getMappingName());
					if (mapping.isEmpty()) {
						logger.warn("MappingAdd: cannot add");
						return new LambdaResponse(BAD_REQUEST, "Error creating new mapping", APPLICATION_JSON_VALUE);
					}
					return new LambdaResponse(
							OK, moshi.adapter(AttributeMap.class).toJson(mapping.get()), APPLICATION_JSON_VALUE);
				})
				.orElseGet(() -> new LambdaResponse(BAD_REQUEST, "Error adding new mapping", APPLICATION_JSON_VALUE));
	}
}
