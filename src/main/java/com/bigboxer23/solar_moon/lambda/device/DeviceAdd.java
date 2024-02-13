package com.bigboxer23.solar_moon.lambda.device;

import com.bigboxer23.solar_moon.data.Device;
import com.bigboxer23.solar_moon.lambda.MethodHandler;
import com.bigboxer23.solar_moon.lambda.data.LambdaRequest;
import com.bigboxer23.solar_moon.lambda.data.LambdaResponse;
import com.bigboxer23.solar_moon.util.TokenGenerator;
import java.io.IOException;
import java.util.Optional;

/** */
public class DeviceAdd extends MethodHandler {
	@Override
	public LambdaResponse handleLambdaRequest(LambdaRequest request) throws IOException {
		return Optional.ofNullable(moshi.adapter(Device.class).fromJson(request.getBody()))
				.map(device -> {
					device.setClientId(getCustomerIdFromRequest(request));
					device.setId(TokenGenerator.generateNewToken());
					if (!deviceComponent.isValidAdd(device)) {
						logger.warn(
								"DeviceAdd: " + moshi.adapter(Device.class).toJson(device) + " Device is not valid.");
						return new LambdaResponse(BAD_REQUEST, "Device is not valid.", APPLICATION_JSON_VALUE);
					}
					if (deviceComponent.addDevice(device) == null) {
						return new LambdaResponse(BAD_REQUEST, "Error creating new device", APPLICATION_JSON_VALUE);
					}
					return DeviceGet.getDeviceResponse(deviceComponent.getDevice(device.getId(), device.getClientId()));
				})
				.orElseGet(() -> new LambdaResponse(BAD_REQUEST, "Error creating new device", APPLICATION_JSON_VALUE));
	}
}
