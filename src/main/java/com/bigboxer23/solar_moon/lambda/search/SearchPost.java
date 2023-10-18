package com.bigboxer23.solar_moon.lambda.search;

import com.bigboxer23.solar_moon.lambda.MethodHandler;
import com.bigboxer23.solar_moon.lambda.data.LambdaRequest;
import com.bigboxer23.solar_moon.lambda.data.LambdaResponse;
import com.bigboxer23.solar_moon.lambda.utils.PropertyUtils;
import com.bigboxer23.solar_moon.open_search.OpenSearchUtils;
import com.bigboxer23.solar_moon.open_search.SearchJSON;
import java.io.IOException;
import java.util.Optional;
import software.amazon.awssdk.utils.StringUtils;

/** */
public class SearchPost extends MethodHandler {

	private String customerIdOverride = PropertyUtils.getProperty("customer.id.override");

	@Override
	public LambdaResponse handleLambdaRequest(LambdaRequest request) throws IOException {
		return Optional.ofNullable(moshi.adapter(SearchJSON.class).fromJson(request.getBody()))
				.map(searchJSON -> {
					searchJSON.setCustomerId(
							StringUtils.isBlank(customerIdOverride)
									? getCustomerIdFromRequest(request)
									: customerIdOverride);
					return new LambdaResponse(
							OK, OpenSearchUtils.queryToJson(OSComponent.search(searchJSON)), APPLICATION_JSON_VALUE);
				})
				.orElseGet(() -> {
					logger.warn("SearchPost: Bad Request.");
					return new LambdaResponse(BAD_REQUEST, "Bad Request", APPLICATION_JSON_VALUE);
				});
	}
}
