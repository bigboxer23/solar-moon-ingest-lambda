package com.bigboxer23.solar_moon.lambda.alarm;

import com.bigboxer23.solar_moon.data.Alarm;
import com.bigboxer23.solar_moon.lambda.MethodHandler;
import com.bigboxer23.solar_moon.lambda.data.LambdaRequest;
import com.bigboxer23.solar_moon.lambda.data.LambdaResponse;
import com.bigboxer23.solar_moon.lambda.device.DeviceGet;
import com.squareup.moshi.Types;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

/** */
public class AlarmGet extends MethodHandler {
	@Override
	public LambdaResponse handleLambdaRequest(LambdaRequest request) throws IOException {
		if (request.getPath().equals("/alarms/") || request.getPath().equals("/alarms")) {
			List<Alarm> devices = alarmComponent.getAlarms(getCustomerIdFromRequest(request));
			return new LambdaResponse(
					OK,
					moshi.adapter(Types.newParameterizedType(List.class, Alarm.class))
							.toJson(devices),
					APPLICATION_JSON_VALUE);
		}
		return getDeviceResponse(alarmComponent.findAlarmByAlarmId(
				DeviceGet.idFromPath(request.getPath()), getCustomerIdFromRequest(request)));
	}

	public static LambdaResponse getDeviceResponse(Optional<Alarm> alarm) {
		return new LambdaResponse(
				alarm.map(a -> OK).orElse(NOT_FOUND),
				alarm.map(a -> moshi.adapter(Alarm.class).toJson(a)).orElse(null),
				APPLICATION_JSON_VALUE);
	}
}
