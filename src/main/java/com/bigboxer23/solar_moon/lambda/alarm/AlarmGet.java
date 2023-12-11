package com.bigboxer23.solar_moon.lambda.alarm;

import com.bigboxer23.solar_moon.data.Alarm;
import com.bigboxer23.solar_moon.data.Device;
import com.bigboxer23.solar_moon.lambda.MethodHandler;
import com.bigboxer23.solar_moon.lambda.data.LambdaRequest;
import com.bigboxer23.solar_moon.lambda.data.LambdaResponse;
import com.bigboxer23.solar_moon.lambda.device.DeviceGet;
import com.squareup.moshi.Types;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/** */
public class AlarmGet extends MethodHandler {
	@Override
	public LambdaResponse handleLambdaRequest(LambdaRequest request) throws IOException {
		if (request.getPath().equals("/alarms/") || request.getPath().equals("/alarms")) {
			return new LambdaResponse(
					OK,
					moshi.adapter(Types.newParameterizedType(List.class, Alarm.class))
							.toJson(fillAlarmData(alarmComponent.getAlarms(getCustomerIdFromRequest(request)))),
					APPLICATION_JSON_VALUE);
		}
		return getAlarmResponse(alarmComponent.findAlarmByAlarmId(
				DeviceGet.idFromPath(request.getPath()), getCustomerIdFromRequest(request)));
	}

	public LambdaResponse getAlarmResponse(Optional<Alarm> alarm) {
		return new LambdaResponse(
				alarm.map(a -> OK).orElse(NOT_FOUND),
				alarm.map(a -> {
							fillAlarmData(null, a);
							return a;
						})
						.map(a -> moshi.adapter(Alarm.class).toJson(a))
						.orElse(null),
				APPLICATION_JSON_VALUE);
	}

	protected static List<Alarm> fillAlarmData(List<Alarm> alarms) {
		if (!alarms.isEmpty()) {
			Map<String, Device> devices =
					deviceComponent.getDevicesForCustomerId(alarms.getFirst().getCustomerId()).stream()
							.collect(Collectors.toMap(Device::getId, Function.identity()));
			alarms.forEach(a -> fillAlarmData(devices, a));
		}
		return alarms;
	}

	private static void fillAlarmData(Map<String, Device> devices, Alarm alarm) {
		Device device = Optional.ofNullable(devices)
				.map(d -> d.get(alarm.getDeviceId()))
				.orElseGet(() -> deviceComponent.getDevice(alarm.getDeviceId(), alarm.getCustomerId()));
		if (device != null) {
			alarm.setDeviceName(device.getDisplayName());
			alarm.setDeviceSite(device.getSite());
		}
	}
}
