package com.bigboxer23.solar_moon.lambda.alarm;

import com.amazonaws.services.lambda.runtime.Context;
import com.bigboxer23.solar_moon.data.Device;
import com.bigboxer23.solar_moon.lambda.AbstractRequestStreamHandler;
import com.bigboxer23.solar_moon.lambda.data.LambdaRequest;
import com.bigboxer23.solar_moon.lambda.data.LambdaResponse;
import com.bigboxer23.solar_moon.lambda.utils.PropertyUtils;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.SendMessageBatchRequest;
import software.amazon.awssdk.services.sqs.model.SendMessageBatchRequestEntry;

/** */
public class ScheduleDeviceCheck extends AbstractRequestStreamHandler {

	private static final String queueUrl = PropertyUtils.getProperty("device.check.sqs.url");

	@Override
	public LambdaResponse handleLambdaRequest(LambdaRequest request) throws IOException {
		return null;
	}

	public void handleRequest(InputStream inputStream, OutputStream outputStream, Context context) throws IOException {
		logger.info("Scheduling device check");
		try (SqsClient sqs = SqsClient.create()) {
			List<SendMessageBatchRequestEntry> entries = new ArrayList<>();
			deviceComponent.getDevices(false).stream()
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
			logger.info("Device check scheduled");
		} catch (Exception e) {
			logger.warn("handleRequest:", e);
		}
		after();
	}

	private void sendMessages(List<SendMessageBatchRequestEntry> entries, SqsClient sqs) {
		logger.info("sending batch to sqs " + entries.size());
		sqs.sendMessageBatch(SendMessageBatchRequest.builder()
				.queueUrl(queueUrl)
				.entries(entries)
				.build());
	}
}
