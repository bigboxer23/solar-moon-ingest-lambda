package com.bigboxer23.solar_moon.lambda.mapping;

import com.bigboxer23.solar_moon.lambda.MethodHandler;
import com.bigboxer23.solar_moon.lambda.MethodPipe;
import io.swagger.v3.oas.models.PathItem;
import java.util.HashMap;
import java.util.Map;

/** */
public class MappingEndpoint extends MethodPipe {
	private static final Map<String, MethodHandler> functions = new HashMap<>();

	static {
		functions.put(PathItem.HttpMethod.GET.name(), new MappingGet());
		functions.put(PathItem.HttpMethod.PUT.name(), new MappingAdd());
		functions.put(PathItem.HttpMethod.DELETE.name(), new MappingDelete());
	}

	@Override
	public Map<String, MethodHandler> getFunction() {
		return functions;
	}
}
