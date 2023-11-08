package com.bigboxer23.solar_moon.lambda.alarm;

import com.bigboxer23.solar_moon.data.Alarm;
import com.bigboxer23.solar_moon.lambda.MethodHandler;
import com.bigboxer23.solar_moon.lambda.data.LambdaRequest;
import com.bigboxer23.solar_moon.lambda.data.LambdaResponse;
import com.bigboxer23.solar_moon.open_search.SearchJSON;
import com.squareup.moshi.Types;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

/** */
public class AlarmSearchPost extends MethodHandler {
	@Override
	public LambdaResponse handleLambdaRequest(LambdaRequest request) throws IOException {
		return Optional.ofNullable(moshi.adapter(SearchJSON.class).fromJson(request.getBody()))
				.map(searchJSON -> {
					searchJSON.setCustomerId(getCustomerIdFromRequest(request));
					return new LambdaResponse(
							OK,
							moshi.adapter(Types.newParameterizedType(List.class, Alarm.class))
									.toJson(alarmComponent.filterAlarms(
											searchJSON.getCustomerId(),
											searchJSON.getSite(),
											searchJSON.getDeviceId())),
							APPLICATION_JSON_VALUE);
				})
				.orElseGet(() -> {
					logger.warn("AlarmSearchPost: Bad Request.");
					return new LambdaResponse(BAD_REQUEST, "Bad Request", APPLICATION_JSON_VALUE);
				});
	}
}
