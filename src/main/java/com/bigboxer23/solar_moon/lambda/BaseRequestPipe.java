package com.bigboxer23.solar_moon.lambda;

import com.bigboxer23.solar_moon.lambda.alarm.AlarmEndpoint;
import com.bigboxer23.solar_moon.lambda.billing.BillingEndpoint;
import com.bigboxer23.solar_moon.lambda.customer.CustomerEndpoint;
import com.bigboxer23.solar_moon.lambda.data.LambdaRequest;
import com.bigboxer23.solar_moon.lambda.data.LambdaResponse;
import com.bigboxer23.solar_moon.lambda.device.DeviceEndpoint;
import com.bigboxer23.solar_moon.lambda.download.DownloadEndpoint;
import com.bigboxer23.solar_moon.lambda.ingest.UploadFunction;
import com.bigboxer23.solar_moon.lambda.mapping.MappingEndpoint;
import com.bigboxer23.solar_moon.lambda.overview.OverviewEndpoint;
import com.bigboxer23.solar_moon.lambda.search.SearchEndpoint;
import com.bigboxer23.solar_moon.lambda.sites.SitesEndpoint;
import com.bigboxer23.solar_moon.lambda.subscription.SubscriptionEndpoint;
import com.bigboxer23.solar_moon.web.TransactionUtil;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/** */
public class BaseRequestPipe extends AbstractRequestStreamHandler {
	private static final Map<String, AbstractRequestStreamHandler> functions = new HashMap<>();

	static {
		functions.put("/billing", new BillingEndpoint());
		functions.put("/customer", new CustomerEndpoint());
		functions.put("/devices", new DeviceEndpoint());
		functions.put("/search", new SearchEndpoint());
		functions.put("/upload", new UploadFunction());
		functions.put("/subscription", new SubscriptionEndpoint());
		functions.put("/alarms", new AlarmEndpoint());
		functions.put("/mappings", new MappingEndpoint());
		functions.put("/overview", new OverviewEndpoint());
		functions.put("/sites", new SitesEndpoint());
		functions.put("/download", new DownloadEndpoint());
	}

	public LambdaResponse handleLambdaRequest(LambdaRequest request) throws IOException {
		AbstractRequestStreamHandler handler = findMatchingPath(getPath(request));
		if (handler == null) {
			logger.warn("Not able to service " + getPath(request));
			return new LambdaResponse(BAD_REQUEST, null, APPLICATION_JSON_VALUE);
		}
		TransactionUtil.updateServiceCalled(handler.getClass().getSimpleName());
		return handler.handleLambdaRequest(request);
	}

	@Override
	public boolean isPricingRedirectEnabled(LambdaRequest request) {
		AbstractRequestStreamHandler handler = findMatchingPath(getPath(request));
		return handler == null || handler.isPricingRedirectEnabled(request);
	}

	private String getPath(LambdaRequest request) {
		if (request.getPath().equals("/")) {
			return request.getRequestContext().getPath();
		}
		return request.getPath();
	}

	private AbstractRequestStreamHandler findMatchingPath(String path) {
		return functions.keySet().stream()
				.filter(path::startsWith)
				.findAny()
				.map(functions::get)
				.orElse(null);
	}
}
