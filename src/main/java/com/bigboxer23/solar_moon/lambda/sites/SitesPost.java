package com.bigboxer23.solar_moon.lambda.sites;

import com.bigboxer23.solar_moon.lambda.MethodHandler;
import com.bigboxer23.solar_moon.lambda.data.LambdaRequest;
import com.bigboxer23.solar_moon.lambda.data.LambdaResponse;
import com.bigboxer23.solar_moon.search.SearchJSON;
import java.io.IOException;

/** */
public class SitesPost extends MethodHandler {
	public LambdaResponse handleLambdaRequest(LambdaRequest request) throws IOException {
		SearchJSON search = moshi.adapter(SearchJSON.class).fromJson(request.getBody());
		if (search == null) {
			return new LambdaResponse(BAD_REQUEST, null, APPLICATION_JSON_VALUE);
		}
		search.setCustomerId(getCustomerIdFromRequest(request));
		return new LambdaResponse(
				OK, gson.toJson(sitesOverviewComponent.getSitesOverviewData(search)), APPLICATION_JSON_VALUE);
	}
}
