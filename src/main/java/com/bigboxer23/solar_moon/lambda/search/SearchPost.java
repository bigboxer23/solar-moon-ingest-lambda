package com.bigboxer23.solar_moon.lambda.search;

import com.bigboxer23.solar_moon.data.Customer;
import com.bigboxer23.solar_moon.lambda.MethodHandler;
import com.bigboxer23.solar_moon.lambda.data.LambdaRequest;
import com.bigboxer23.solar_moon.lambda.data.LambdaResponse;
import com.bigboxer23.solar_moon.lambda.utils.PropertyUtils;
import com.bigboxer23.solar_moon.search.OpenSearchUtils;
import com.bigboxer23.solar_moon.search.SearchJSON;
import com.squareup.moshi.Types;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import software.amazon.awssdk.utils.StringUtils;

/** */
public class SearchPost extends MethodHandler {

	private final String customerIdOverride = PropertyUtils.getProperty("customer.id.override");

	@Override
	public LambdaResponse handleLambdaRequest(LambdaRequest request) throws IOException {
		String body = request.getBody();
		String customerId = getCustomerId(request);
		if (body.trim().startsWith("[")) {
			return Optional.ofNullable(
							moshi.<List<SearchJSON>>adapter(Types.newParameterizedType(List.class, SearchJSON.class))
									.fromJson(body))
					.map(searchJSONs -> new LambdaResponse(
							OK,
							"["
									+ String.join(
											",",
											searchJSONs.stream()
													.map(searchJSON -> getSearchResponse(searchJSON, customerId))
													.toList())
									+ "]",
							APPLICATION_JSON_VALUE))
					.orElseGet(this::badRequest);
		}
		return Optional.ofNullable(moshi.adapter(SearchJSON.class).fromJson(body))
				.map(searchJSON ->
						new LambdaResponse(OK, getSearchResponse(searchJSON, customerId), APPLICATION_JSON_VALUE))
				.orElseGet(this::badRequest);
	}

	private String getSearchResponse(SearchJSON json, String customerId) {
		json.setCustomerId(customerId);
		return OpenSearchUtils.queryToJson(OSComponent.search(json));
	}

	private LambdaResponse badRequest() {
		logger.warn("SearchPost: Bad Request.");
		return new LambdaResponse(BAD_REQUEST, "Bad Request", APPLICATION_JSON_VALUE);
	}

	private String getCustomerId(LambdaRequest request) {
		String customerId = getCustomerIdFromRequest(request);
		if (!StringUtils.isBlank(customerIdOverride)
				&& customerComponent
						.findCustomerByCustomerId(customerId)
						.map(Customer::isAdmin)
						.orElse(false)) {
			return customerIdOverride;
		}
		return customerId;
	}
}
