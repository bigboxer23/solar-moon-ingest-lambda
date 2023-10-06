package com.bigboxer23.solar_moon.lambda.device;

import com.bigboxer23.solar_moon.data.Device;
import com.bigboxer23.solar_moon.lambda.MethodHandler;
import com.bigboxer23.solar_moon.lambda.data.LambdaRequest;
import com.bigboxer23.solar_moon.lambda.data.LambdaResponse;
import com.squareup.moshi.Types;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

/** */
public class DeviceGet extends MethodHandler {
	@Override
	public LambdaResponse handleLambdaRequest(LambdaRequest request) throws IOException {
		if (request.getPath().equals("/devices/") || request.getPath().equals("/devices")) {
			List<Device> devices = deviceComponent.getDevices(getCustomerIdFromRequest(request));
			return new LambdaResponse(
					devices.isEmpty() ? NOT_FOUND : OK,
					devices.isEmpty()
							? "No devices available"
							: moshi.adapter(Types.newParameterizedType(List.class, Device.class))
									.toJson(devices),
					APPLICATION_JSON_VALUE);
		}
		return getDeviceResponse(
				deviceComponent.getDevice(deviceIdFromPath(request.getPath()), getCustomerIdFromRequest(request)));
	}

	public static LambdaResponse getDeviceResponse(Device device) {
		return new LambdaResponse(
				device == null ? NOT_FOUND : OK,
				device == null ? null : moshi.adapter(Device.class).toJson(device),
				APPLICATION_JSON_VALUE);
	}

	public static String deviceIdFromPath(String path) {
		return Optional.ofNullable(path)
				.map(p -> p.split("/"))
				.map(p -> p[p.length - 1])
				.orElse(null);
	}
}
