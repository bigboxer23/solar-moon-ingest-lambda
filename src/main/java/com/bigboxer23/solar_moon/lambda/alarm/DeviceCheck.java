package com.bigboxer23.solar_moon.lambda.alarm;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.SQSBatchResponse;
import com.amazonaws.services.lambda.runtime.events.SQSEvent;
import com.bigboxer23.solar_moon.MeterConstants;
import com.bigboxer23.solar_moon.data.Device;
import com.bigboxer23.solar_moon.data.DeviceData;
import com.bigboxer23.solar_moon.lambda.AbstractLambdaHandler;
import com.bigboxer23.solar_moon.open_search.OpenSearchQueries;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/** */
public class DeviceCheck extends AbstractLambdaHandler implements RequestHandler<SQSEvent, SQSBatchResponse> {
	public static final long THIRTY_MINUTES = 60 * 30 * 1000;

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
		Device device = moshi.adapter(Device.class).fromJson(body);
		if (device == null) {
			logger.warn("error getting device:\n" + body);
			return;
		}
		DeviceData data =
				OSComponent.getLastDeviceEntry(device.getName(), OpenSearchQueries.getDeviceIdQuery(device.getId()));
		if (data == null) {
			logger.debug("likely new device with no data " + device.getId());
			return;
		}
		if (!device.isDisabled()
				&& data.getDate().getTime() < new Date(System.currentTimeMillis() - THIRTY_MINUTES).getTime()) {
			alarmComponent.alarmConditionDetected(
					data.getCustomerId(),
					data,
					"No data recently from device.  Last data: "
							+ new SimpleDateFormat(MeterConstants.DATE_PATTERN).format(data.getDate()));
		}
	}
}
