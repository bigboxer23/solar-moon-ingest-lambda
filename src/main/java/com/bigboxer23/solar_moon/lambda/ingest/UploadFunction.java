package com.bigboxer23.solar_moon.lambda.ingest;

import com.bigboxer23.solar_moon.data.DeviceData;
import com.bigboxer23.solar_moon.ingest.*;
import com.bigboxer23.solar_moon.lambda.AbstractRequestStreamHandler;
import com.bigboxer23.solar_moon.lambda.data.LambdaRequest;
import com.bigboxer23.solar_moon.lambda.data.LambdaResponse;
import com.bigboxer23.solar_moon.web.AuthenticationUtils;
import com.bigboxer23.solar_moon.web.TransactionUtil;
import java.io.*;
import javax.xml.xpath.XPathExpressionException;
import lombok.extern.slf4j.Slf4j;
import org.opensearch.client.ResponseException;

/** */
@Slf4j
public class UploadFunction extends AbstractRequestStreamHandler implements MeterConstants {

	@Override
	public LambdaResponse handleLambdaRequest(LambdaRequest request) throws IOException {
		String customerId = AuthenticationUtils.authenticateRequest(request, customerComponent);
		if (customerId == null) {
			return new LambdaResponse(UNAUTHORIZED, XML_FAILURE_RESPONSE, TEXT_XML);
		}

		try {
			if (!obviousIngestComponent.isUpdateEvent(request.getBody())) {
				return new LambdaResponse(OK, XML_SUCCESS_RESPONSE, TEXT_XML);
			}
			DeviceData data = obviousIngestComponent.handleDeviceBody(request.getBody(), customerId);
			if (data == null) {
				// Return OK here because the device will stack the bad request and resend unless we
				// acknowledge we received it
				return new LambdaResponse(OK, XML_SUCCESS_RESPONSE, TEXT_XML);
			}
			TransactionUtil.addDeviceId(data.getDeviceId(), data.getSiteId());
			log.info("successfully uploaded data: " + data.getDate());
			return new LambdaResponse(OK, XML_SUCCESS_RESPONSE, TEXT_XML);
		} catch (XPathExpressionException | ResponseException e) {
			log.warn("handleRequest", e);
			return new LambdaResponse(BAD_REQUEST, XML_FAILURE_RESPONSE, TEXT_XML);
		}
	}

	@Override
	protected boolean isPricingRedirectEnabled(LambdaRequest request) {
		return false;
	}
}
