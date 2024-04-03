package com.bigboxer23.solar_moon.lambda.alarm;

import com.amazonaws.services.lambda.runtime.Context;
import com.bigboxer23.solar_moon.lambda.AbstractRequestStreamHandler;
import com.bigboxer23.solar_moon.lambda.data.LambdaRequest;
import com.bigboxer23.solar_moon.lambda.data.LambdaResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/** */
public class ScheduledAlarmCleanUp extends AbstractRequestStreamHandler {
	@Override
	public LambdaResponse handleLambdaRequest(LambdaRequest request) throws IOException {
		return null;
	}

	@Override
	public void handleRequest(InputStream inputStream, OutputStream outputStream, Context context) {
		safeHandleRequest(() -> {
			alarmComponent.cleanupOldAlarms();
			return null;
		});
	}
}
