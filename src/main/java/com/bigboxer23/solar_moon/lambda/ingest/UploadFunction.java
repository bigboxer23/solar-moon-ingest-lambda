package com.bigboxer23.solar_moon.lambda.ingest;

import com.bigboxer23.solar_moon.ingest.*;
import com.bigboxer23.solar_moon.data.DeviceData;
import com.bigboxer23.solar_moon.lambda.AbstractRequestStreamHandler;
import com.bigboxer23.solar_moon.lambda.data.LambdaRequest;
import com.bigboxer23.solar_moon.lambda.data.LambdaResponse;
import com.bigboxer23.solar_moon.web.AuthenticationUtils;
import java.io.*;
import javax.xml.xpath.XPathExpressionException;

/** */
public class UploadFunction extends AbstractRequestStreamHandler implements MeterConstants {

	@Override
	public LambdaResponse handleLambdaRequest(LambdaRequest request) throws IOException {
		String customerId = AuthenticationUtils.authenticateRequest(request, customerComponent);
		if (customerId == null) {
			return new LambdaResponse(UNAUTHORIZED, XML_FAILURE_RESPONSE, TEXT_XML);
		}

		try {
			if (!generationComponent.isUpdateEvent(request.getBody())) {
				return new LambdaResponse(OK, XML_SUCCESS_RESPONSE, TEXT_XML);
			}
			DeviceData data = generationComponent.handleDeviceBody(request.getBody(), customerId);
			if (data == null) {
				// Return OK here because the device will stack the bad request and resend unless we
				// acknowledge we received it
				return new LambdaResponse(OK, XML_SUCCESS_RESPONSE, TEXT_XML);
			}
			logger.info("successfully uploaded data: " + data.getName() + " : " + data.getDate());
			return new LambdaResponse(OK, XML_SUCCESS_RESPONSE, TEXT_XML);
		} catch (XPathExpressionException e) {
			logger.warn("handleRequest:", e);
			return new LambdaResponse(BAD_REQUEST, XML_FAILURE_RESPONSE, TEXT_XML);
		}
	}

	@Override
	protected boolean isPricingRedirectEnabled(LambdaRequest request) {
		return false;
	}
}
