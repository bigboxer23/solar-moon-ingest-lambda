package com.bigboxer23.solar_moon.lambda.overview;

import com.bigboxer23.solar_moon.data.Device;
import com.bigboxer23.solar_moon.lambda.MethodHandler;
import com.bigboxer23.solar_moon.lambda.data.LambdaRequest;
import com.bigboxer23.solar_moon.lambda.data.LambdaResponse;
import com.bigboxer23.solar_moon.search.OpenSearchConstants;
import com.bigboxer23.solar_moon.search.SearchJSON;
import com.bigboxer23.solar_moon.util.TimeConstants;
import com.bigboxer23.solar_moon.util.TimeUtils;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import software.amazon.awssdk.utils.StringUtils;

/** */
public class OverviewPost extends MethodHandler {
	@Override
	public LambdaResponse handleLambdaRequest(LambdaRequest request) throws IOException {
		long time = System.currentTimeMillis();
		String customerId = getCustomerIdFromRequest(request);
		SearchJSON search = moshi.adapter(SearchJSON.class).fromJson(request.getBody());
		if (search == null) {
			return new LambdaResponse(BAD_REQUEST, null, APPLICATION_JSON_VALUE);
		}
		search.setCustomerId(customerId);
		OverviewData data = new OverviewData();
		data.setAlarms(alarmComponent.getAlarms(customerId));
		data.setDevices(deviceComponent.getDevicesForCustomerId(customerId));
		search.setVirtual(true);
		data.setOverall(getData(null, search));
		fillSiteInfo(data, search);
		fillInOverallInfo(data, search);
		logger.debug("time: " + (System.currentTimeMillis() - time));
		return new LambdaResponse(OK, gson.toJson(data), APPLICATION_JSON_VALUE);
	}

	private void fillSiteInfo(OverviewData data, SearchJSON searchJson) {
		if (data == null || data.getDevices() == null) {
			logger.warn("data or devices null, cannot fill data");
			return;
		}
		data.setSitesOverviewData(new HashMap<>());
		data.getDevices().stream().filter(Device::isVirtual).forEach(site -> data.getSitesOverviewData()
				.put(site.getDisplayName(), getData(site.getDisplayName(), searchJson)));
	}

	private void fillInOverallInfo(OverviewData data, SearchJSON searchJson) {
		if (searchJson == null || StringUtils.isBlank(searchJson.getTimeZone())) {
			return;
		}
		Date end = TimeUtils.getStartOfDay(searchJson.getTimeZone());
		searchJson.setDeviceName(null);
		searchJson.setEndDate(end.getTime());
		searchJson.setStartDate(end.getTime() - TimeConstants.DAY);
		searchJson.setType(OpenSearchConstants.AT_SEARCH_TYPE);
		data.getOverall().setDailyEnergyConsumedTotal(OSComponent.search(searchJson));
		data.getOverall().setDailyEnergyConsumedAverage(OSComponent.getAverageEnergyConsumedPerDay(searchJson));
	}

	private SiteOverviewData getData(String site, SearchJSON searchJson) {
		SiteOverviewData data = new SiteOverviewData();
		searchJson.setDeviceName(site);
		searchJson.setType(OpenSearchConstants.AT_SEARCH_TYPE);
		data.setTotalAvg(OSComponent.search(searchJson));
		searchJson.setType(OpenSearchConstants.TS_SEARCH_TYPE);
		data.setTimeSeries(OSComponent.search(searchJson));
		return data;
	}
}
