package com.bigboxer23.solar_moon.lambda.customer_creation;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import com.bigboxer23.solar_moon.CustomerComponent;
import com.bigboxer23.solar_moon.lambda.data.CognitoCommon;
import com.squareup.moshi.Moshi;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import org.apache.commons.io.IOUtils;

/** */
public class PreUserCreation implements RequestStreamHandler {
	private static final Moshi moshi = new Moshi.Builder().build();

	private static final CustomerComponent component = new CustomerComponent();

	@Override
	public void handleRequest(InputStream inputStream, OutputStream outputStream, Context context) throws IOException {
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
				OutputStreamWriter writer = new OutputStreamWriter(outputStream, StandardCharsets.UTF_8)) {
			String rawRequest = IOUtils.toString(reader);
			System.out.println(rawRequest);
			Optional.ofNullable(moshi.adapter(CognitoCommon.class).fromJson(rawRequest))
					.ifPresent(request -> {
						// TODO: do some validation here or auto approval
						/*component.addCustomer(
						request.getRequest().getUserAttributes().getEmail(),
						request.getRequest().getUserAttributes().getSub());*/
					});
			writer.write(rawRequest);
		}
	}
}
