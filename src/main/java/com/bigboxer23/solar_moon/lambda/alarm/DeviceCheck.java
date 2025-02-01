package com.bigboxer23.solar_moon.lambda.alarm;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.SQSBatchResponse;
import com.amazonaws.services.lambda.runtime.events.SQSEvent;
import com.bigboxer23.solar_moon.data.Device;
import com.bigboxer23.solar_moon.lambda.AbstractLambdaHandler;
import com.bigboxer23.solar_moon.web.TransactionUtil;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

/** */
@Slf4j
public class DeviceCheck extends AbstractLambdaHandler implements RequestHandler<SQSEvent, SQSBatchResponse> {
	@Override
	public SQSBatchResponse handleRequest(SQSEvent sqsEvent, Context context) {
		TransactionUtil.updateServiceCalled(getClass().getSimpleName());
		log.info("Processing " + sqsEvent.getRecords().size() + " from queue");
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
		Device device = moshi.adapter(Device.class).fromJson(body);
		alarmComponent.checkDevice(device);
		if (device == null) {
			log.warn("error getting device:\n" + body);
		}
	}
}
