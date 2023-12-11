package com.bigboxer23.solar_moon.lambda.overview;

import lombok.Data;
import org.opensearch.client.opensearch.core.SearchResponse;

/** */
@Data
public class SiteOverviewData {
	private SearchResponse totalAvg;

	private SearchResponse timeSeries;
}
