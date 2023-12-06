package com.bigboxer23.solar_moon.lambda.mapping;

import com.bigboxer23.solar_moon.lambda.MethodHandler;
import com.bigboxer23.solar_moon.lambda.data.LambdaRequest;
import com.bigboxer23.solar_moon.lambda.data.LambdaResponse;
import com.bigboxer23.solar_moon.mapping.AttributeMap;
import com.squareup.moshi.Types;
import java.io.IOException;
import java.util.List;

/** */
public class MappingGet extends MethodHandler {
	@Override
	public LambdaResponse handleLambdaRequest(LambdaRequest request) throws IOException {
		return new LambdaResponse(
				OK,
				moshi.adapter(Types.newParameterizedType(List.class, AttributeMap.class))
						.toJson(mappingComponent.getMappings(getCustomerIdFromRequest(request))),
				APPLICATION_JSON_VALUE);
	}
}
