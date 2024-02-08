package com.bigboxer23.solar_moon.lambda.search;

import com.bigboxer23.solar_moon.lambda.MethodHandler;
import com.bigboxer23.solar_moon.lambda.data.LambdaRequest;
import com.bigboxer23.solar_moon.lambda.data.LambdaResponse;
import com.bigboxer23.solar_moon.search.SearchJSON;
import com.squareup.moshi.Types;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import org.opensearch.client.opensearch.core.SearchResponse;

/** */
public class SearchPost extends MethodHandler {
	@Override
	public LambdaResponse handleLambdaRequest(LambdaRequest request) throws IOException {
		String body = request.getBody();
		String customerId = getCustomerIdFromRequest(request);
		if (body.trim().startsWith("[")) {
			return Optional.ofNullable(
							moshi.<List<SearchJSON>>adapter(Types.newParameterizedType(List.class, SearchJSON.class))
									.fromJson(body))
					.map(searchJSONs -> new LambdaResponse(
							OK,
							gson.toJson(searchJSONs.stream()
									.map(searchJSON -> getSearchResponse(searchJSON, customerId))
									.toList()),
							APPLICATION_JSON_VALUE))
					.orElseGet(this::badRequest);
		}
		return Optional.ofNullable(moshi.adapter(SearchJSON.class).fromJson(body))
				.map(searchJSON -> new LambdaResponse(
						OK, gson.toJson(getSearchResponse(searchJSON, customerId)), APPLICATION_JSON_VALUE))
				.orElseGet(this::badRequest);
	}

	private SearchResponse getSearchResponse(SearchJSON json, String customerId) {
		json.setCustomerId(customerId);
		return OSComponent.search(json);
	}

	private LambdaResponse badRequest() {
		logger.warn("SearchPost: Bad Request.");
		return new LambdaResponse(BAD_REQUEST, "Bad Request", APPLICATION_JSON_VALUE);
	}
}
