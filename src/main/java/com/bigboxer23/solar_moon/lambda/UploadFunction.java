package com.bigboxer23.solar_moon.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.bigboxer23.solar_moon.*;
import com.bigboxer23.solar_moon.data.DeviceData;
import com.bigboxer23.solar_moon.lambda.data.LambdaRequest;
import com.bigboxer23.solar_moon.lambda.data.LambdaResponse;
import com.bigboxer23.solar_moon.open_search.OpenSearchComponent;
import com.bigboxer23.solar_moon.web.AuthenticationUtils;
import com.bigboxer23.solar_moon.web.Transaction;
import com.bigboxer23.solar_moon.web.TransactionUtil;
import java.io.*;
import java.nio.charset.StandardCharsets;
import javax.xml.xpath.XPathExpressionException;
import org.apache.commons.io.IOUtils;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

/** */
public class UploadFunction extends AbstractRequestStreamHandler implements MeterConstants {
	private static final GenerationMeterComponent component = getComponent();

	private void sendResponse(String message, int errorCode, OutputStreamWriter writer) throws IOException {
		writer.write(toJSONString(new LambdaResponse(errorCode, message, MediaType.TEXT_XML.toString())));
	}

	private String toJSONString(LambdaResponse response) {
		return moshi.adapter(LambdaResponse.class).toJson(response);
	}

	private static GenerationMeterComponent getComponent() {
		OpenSearchComponent OSComponent = new OpenSearchComponent(new StandardEnvironment());
		DeviceComponent deviceComponent = new DeviceComponent();
		OpenWeatherComponent weatherComponent = new OpenWeatherComponent();
		return new GenerationMeterComponent(
				OSComponent,
				new AlarmComponent(weatherComponent),
				new DeviceComponent(),
				new SiteComponent(OSComponent, deviceComponent));
	}

	@Transaction
	@Override
	public void handleRequest(InputStream inputStream, OutputStream outputStream, Context theContext)
			throws IOException {
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
				OutputStreamWriter writer = new OutputStreamWriter(outputStream, StandardCharsets.UTF_8)) {
			try {
				LambdaRequest request = moshi.adapter(LambdaRequest.class).fromJson(IOUtils.toString(reader));
				TransactionUtil.setRemoteFromLambdaRequest(request);
				logger.debug("request: " + moshi.adapter(LambdaRequest.class).toJson(request));
				String customerId = AuthenticationUtils.authenticateRequest(request, customerComponent);
				if (customerId == null) {
					sendResponse(XML_FAILURE_RESPONSE, HttpStatus.UNAUTHORIZED.value(), writer);
					return;
				}
				if (!component.isUpdateEvent(request.getBody())) {
					sendResponse(XML_SUCCESS_RESPONSE, HttpStatus.OK.value(), writer);
					return;
				}
				DeviceData data = component.handleDeviceBody(request.getBody(), customerId);
				if (data == null) {
					sendResponse(XML_FAILURE_RESPONSE, HttpStatus.BAD_REQUEST.value(), writer);
					return;
				}
				logger.info("successfully uploaded data: " + data.getName() + " : " + data.getDate());
				sendResponse(XML_SUCCESS_RESPONSE, HttpStatus.OK.value(), writer);
				Thread.sleep(750);
			} catch (IOException | XPathExpressionException | InterruptedException e) {
				logger.warn("handleRequest:", e);
				sendResponse(XML_FAILURE_RESPONSE, HttpStatus.BAD_REQUEST.value(), writer);
			}
		}
	}
}
