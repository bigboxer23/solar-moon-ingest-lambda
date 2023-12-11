package com.bigboxer23.solar_moon.lambda.overview;

import com.bigboxer23.solar_moon.lambda.MethodHandler;
import com.bigboxer23.solar_moon.lambda.MethodPipe;
import io.swagger.v3.oas.models.PathItem;
import java.util.HashMap;
import java.util.Map;

/** */
public class OverviewEndpoint extends MethodPipe {
	private static final Map<String, MethodHandler> functions = new HashMap<>();

	static {
		functions.put(PathItem.HttpMethod.POST.name(), new OverviewPost());
	}

	@Override
	public Map<String, MethodHandler> getFunction() {
		return functions;
	}
}
