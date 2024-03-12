package com.bigboxer23.solar_moon.lambda.device;

import com.bigboxer23.solar_moon.data.Device;
import com.bigboxer23.solar_moon.lambda.MethodHandler;
import com.bigboxer23.solar_moon.lambda.data.LambdaRequest;
import com.bigboxer23.solar_moon.lambda.data.LambdaResponse;
import java.io.IOException;
import java.util.Optional;

/** */
public class DeviceUpdate extends MethodHandler {
	@Override
	public LambdaResponse handleLambdaRequest(LambdaRequest request) throws IOException {
		return Optional.ofNullable(moshi.adapter(Device.class).fromJson(request.getBody()))
				.map(device -> {
					device.setClientId(getCustomerIdFromRequest(request));
					if (!deviceComponent.isValidUpdate(device)) {
						logger.warn("DeviceUpdate: "
								+ moshi.adapter(Device.class).toJson(device)
								+ " Device is not valid.");
						return new LambdaResponse(BAD_REQUEST, "Device is not valid.", APPLICATION_JSON_VALUE);
					}
					deviceComponent.updateDevice(device);
					return DeviceGet.getDeviceResponse(
							deviceComponent.findDeviceById(device.getId(), device.getClientId()));
				})
				.orElseGet(() -> {
					logger.warn("DeviceUpdate: Could not find device to update.");
					return new LambdaResponse(NOT_FOUND, "Could not find device to update", APPLICATION_JSON_VALUE);
				});
	}
}
