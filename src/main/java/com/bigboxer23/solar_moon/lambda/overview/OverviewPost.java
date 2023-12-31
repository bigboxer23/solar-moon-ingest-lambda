package com.bigboxer23.solar_moon.lambda.overview;

import com.bigboxer23.solar_moon.lambda.MethodHandler;
import com.bigboxer23.solar_moon.lambda.data.LambdaRequest;
import com.bigboxer23.solar_moon.lambda.data.LambdaResponse;
import com.bigboxer23.solar_moon.search.SearchJSON;
import java.io.IOException;

/** */
public class OverviewPost extends MethodHandler {
	@Override
	public LambdaResponse handleLambdaRequest(LambdaRequest request) throws IOException {
		SearchJSON search = moshi.adapter(SearchJSON.class).fromJson(request.getBody());
		if (search == null) {
			return new LambdaResponse(BAD_REQUEST, null, APPLICATION_JSON_VALUE);
		}
		search.setCustomerId(getCustomerIdFromRequest(request));
		return new LambdaResponse(OK, gson.toJson(overviewComponent.getOverviewData(search)), APPLICATION_JSON_VALUE);
	}
}
