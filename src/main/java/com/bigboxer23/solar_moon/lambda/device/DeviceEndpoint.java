package com.bigboxer23.solar_moon.lambda.device;

import com.bigboxer23.solar_moon.lambda.MethodHandler;
import com.bigboxer23.solar_moon.lambda.MethodPipe;
import io.swagger.v3.oas.models.PathItem;
import java.util.HashMap;
import java.util.Map;

/** */
public class DeviceEndpoint extends MethodPipe {
	private static final Map<String, MethodHandler> functions = new HashMap<>();

	static {
		functions.put(PathItem.HttpMethod.GET.name(), new DeviceGet());
		functions.put(PathItem.HttpMethod.POST.name(), new DeviceUpdate());
		functions.put(PathItem.HttpMethod.PUT.name(), new DeviceAdd());
		functions.put(PathItem.HttpMethod.DELETE.name(), new DeviceDelete());
	}

	@Override
	public Map<String, MethodHandler> getFunction() {
		return functions;
	}
}
