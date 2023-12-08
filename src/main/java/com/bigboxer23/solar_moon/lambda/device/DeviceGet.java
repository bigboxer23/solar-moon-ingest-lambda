package com.bigboxer23.solar_moon.lambda.device;

import com.bigboxer23.solar_moon.data.Device;
import com.bigboxer23.solar_moon.lambda.MethodHandler;
import com.bigboxer23.solar_moon.lambda.data.LambdaRequest;
import com.bigboxer23.solar_moon.lambda.data.LambdaResponse;
import com.squareup.moshi.Types;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

/** */
public class DeviceGet extends MethodHandler {
	@Override
	public LambdaResponse handleLambdaRequest(LambdaRequest request) throws IOException {
		if (request.getPath().equals("/devices/") || request.getPath().equals("/devices")) {
			List<Device> devices = deviceComponent.getDevicesForCustomerId(getCustomerIdFromRequest(request));
			return new LambdaResponse(
					OK,
					moshi.adapter(Types.newParameterizedType(List.class, Device.class))
							.toJson(devices),
					APPLICATION_JSON_VALUE);
		}
		return getDeviceResponse(
				deviceComponent.getDevice(idFromPath(request.getPath()), getCustomerIdFromRequest(request)));
	}

	public static LambdaResponse getDeviceResponse(Device device) {
		return new LambdaResponse(
				device == null ? NOT_FOUND : OK,
				device == null ? null : moshi.adapter(Device.class).toJson(device),
				APPLICATION_JSON_VALUE);
	}

	public static String idFromPath(String path) {
		return Optional.ofNullable(path)
				.map(p -> p.split("/"))
				.map(p -> p[p.length - 1])
				.map(p -> URLDecoder.decode(p, StandardCharsets.UTF_8))
				.orElse(null);
	}
}
