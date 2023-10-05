package com.bigboxer23.solar_moon.lambda.customer;

import com.bigboxer23.solar_moon.lambda.MethodHandler;
import com.bigboxer23.solar_moon.lambda.MethodPipe;
import io.swagger.v3.oas.models.PathItem;
import java.util.HashMap;
import java.util.Map;

/** */
public class CustomerEndpoint extends MethodPipe {
	private static final Map<String, MethodHandler> functions = new HashMap<>();

	static {
		functions.put(PathItem.HttpMethod.GET.name(), new CustomerGet());
		functions.put(PathItem.HttpMethod.POST.name(), new CustomerUpdate());
	}

	@Override
	public Map<String, MethodHandler> getFunction() {
		return functions;
	}
}
