package com.bigboxer23.solar_moon.lambda.alarm;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.SQSBatchResponse;
import com.amazonaws.services.lambda.runtime.events.SQSEvent;
import com.bigboxer23.solar_moon.data.Device;
import com.bigboxer23.solar_moon.lambda.AbstractLambdaHandler;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/** */
public class DeviceCheck extends AbstractLambdaHandler implements RequestHandler<SQSEvent, SQSBatchResponse> {
	@Override
	public SQSBatchResponse handleRequest(SQSEvent sqsEvent, Context context) {
		logger.warn(
				"starting device processing from queue " + sqsEvent.getRecords().size());
		List<SQSBatchResponse.BatchItemFailure> batchItemFailures = new ArrayList<>();
		String messageId = "";
		for (SQSEvent.SQSMessage message : sqsEvent.getRecords()) {
			try {
				messageId = message.getMessageId();
				handleMessageBody(message.getBody());
			} catch (Exception e) {
				// Add failed message identifier to the batchItemFailures list
				batchItemFailures.add(new SQSBatchResponse.BatchItemFailure(messageId));
			}
		}
		after();
		return new SQSBatchResponse(batchItemFailures);
	}

	private void handleMessageBody(String body) throws IOException {
		alarmComponent.checkDevice(moshi.adapter(Device.class).fromJson(body)).or(() -> {
			logger.warn("error getting device:\n" + body);
			return Optional.empty();
		});
	}
}
