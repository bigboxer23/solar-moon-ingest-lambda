package com.bigboxer23.solar_moon.lambda.customer_creation;

import com.amazonaws.services.lambda.runtime.Context;
import com.bigboxer23.solar_moon.lambda.AbstractRequestStreamHandler;
import com.bigboxer23.solar_moon.lambda.data.CognitoCommon;
import com.bigboxer23.solar_moon.lambda.data.LambdaRequest;
import com.bigboxer23.solar_moon.lambda.data.LambdaResponse;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import org.apache.commons.io.IOUtils;

/** */
public class PostUserCreation extends AbstractRequestStreamHandler {
	@Override
	public LambdaResponse handleLambdaRequest(LambdaRequest request) throws IOException {
		// NOOP b/c we just want to feed the raw data back
		return null;
	}

	@Override
	public void handleRequest(InputStream inputStream, OutputStream outputStream, Context context) throws IOException {
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
				OutputStreamWriter writer = new OutputStreamWriter(outputStream, StandardCharsets.UTF_8)) {
			String rawRequest = IOUtils.toString(reader);
			logger.debug(rawRequest);
			Optional.ofNullable(moshi.adapter(CognitoCommon.class).fromJson(rawRequest))
					.ifPresent(request -> {
						customerComponent.addCustomer(
								request.getRequest().getUserAttributes().getEmail(),
								request.getRequest().getUserAttributes().getSub(),
								request.getRequest().getUserAttributes().getName());
					});
			writer.write(rawRequest);
		}
	}
}
