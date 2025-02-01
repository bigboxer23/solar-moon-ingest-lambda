package com.bigboxer23.solar_moon.lambda.download;

import com.bigboxer23.solar_moon.lambda.MethodHandler;
import com.bigboxer23.solar_moon.lambda.data.LambdaRequest;
import com.bigboxer23.solar_moon.lambda.data.LambdaResponse;
import com.bigboxer23.solar_moon.search.SearchJSON;
import java.io.IOException;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;

/** */
@Slf4j
public class DownloadPost extends MethodHandler {
	@Override
	public LambdaResponse handleLambdaRequest(LambdaRequest request) throws IOException {
		String customerId = getCustomerIdFromRequest(request);
		return Optional.ofNullable(moshi.adapter(SearchJSON.class).fromJson(request.getBody()))
				.map(searchJSON -> {
					searchJSON.setCustomerId(customerId);
					try {
						return new LambdaResponse(
								OK,
								""
										+ downloadComponent.getPageSizeDays(OSComponent.getDevicesFacet(searchJSON)
												.size()),
								APPLICATION_JSON_VALUE);
					} catch (IOException e) {
						log.warn("handleLambdaRequest", e);
					}
					return new LambdaResponse(BAD_REQUEST, null, APPLICATION_JSON_VALUE);
				})
				.orElseGet(() -> new LambdaResponse(BAD_REQUEST, null, APPLICATION_JSON_VALUE));
	}
}
