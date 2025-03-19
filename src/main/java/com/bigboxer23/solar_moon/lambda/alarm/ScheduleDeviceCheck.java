package com.bigboxer23.solar_moon.lambda.alarm;

import com.amazonaws.services.lambda.runtime.Context;
import com.bigboxer23.solar_moon.data.Device;
import com.bigboxer23.solar_moon.lambda.AbstractRequestStreamHandler;
import com.bigboxer23.solar_moon.lambda.data.LambdaRequest;
import com.bigboxer23.solar_moon.lambda.data.LambdaResponse;
import com.bigboxer23.utils.properties.PropertyUtils;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.SendMessageBatchRequest;
import software.amazon.awssdk.services.sqs.model.SendMessageBatchRequestEntry;

/** */
@Slf4j
public class ScheduleDeviceCheck extends AbstractRequestStreamHandler {

	private static final String queueUrl = PropertyUtils.getProperty("device.check.sqs.url");

	@Override
	public LambdaResponse handleLambdaRequest(LambdaRequest request) throws IOException {
		return null;
	}

	@Override
	public void handleRequest(InputStream inputStream, OutputStream outputStream, Context context) throws IOException {
		safeHandleRequest(() -> {
			log.info("Scheduling device check");
			try (SqsClient sqs = SqsClient.create()) {
				List<SendMessageBatchRequestEntry> entries = new ArrayList<>();
				deviceComponent.getDevices(false).stream()
						.filter(device -> !device.isDisabled())
						.map(device -> SendMessageBatchRequestEntry.builder()
								.id(device.getId())
								.messageBody(moshi.adapter(Device.class).toJson(device))
								.build())
						.forEach(entry -> {
							entries.add(entry);
							if (entries.size() == 10) {
								sendMessages(entries, sqs);
								entries.clear();
							}
						});
				if (!entries.isEmpty()) {
					sendMessages(entries, sqs);
				}
				log.info("Device check scheduled");
			}
			alarmComponent.clearDisabledResolvedAlarms();
		});
	}

	private void sendMessages(List<SendMessageBatchRequestEntry> entries, SqsClient sqs) {
		log.debug("sending batch to sqs " + entries.size());
		sqs.sendMessageBatch(SendMessageBatchRequest.builder()
				.queueUrl(queueUrl)
				.entries(entries)
				.build());
	}
}
