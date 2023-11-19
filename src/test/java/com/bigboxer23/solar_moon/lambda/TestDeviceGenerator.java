package com.bigboxer23.solar_moon.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.bigboxer23.solar_moon.TestUtils;
import com.bigboxer23.solar_moon.TimeUtils;
import com.bigboxer23.solar_moon.data.Device;
import com.bigboxer23.solar_moon.data.DeviceData;
import com.bigboxer23.solar_moon.lambda.data.LambdaRequest;
import com.bigboxer23.solar_moon.lambda.data.LambdaResponse;
import com.bigboxer23.solar_moon.lambda.utils.PropertyUtils;
import com.bigboxer23.solar_moon.open_search.OpenSearchQueries;
import com.bigboxer23.solar_moon.web.TransactionUtil;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import javax.xml.xpath.XPathExpressionException;
import software.amazon.awssdk.utils.StringUtils;

/** */
public class TestDeviceGenerator extends AbstractRequestStreamHandler {

	private static final String customerId = PropertyUtils.getProperty("customer_id");

	private static final String srcCustomerId = PropertyUtils.getProperty("customer_id_source");

	@Override
	public LambdaResponse handleLambdaRequest(LambdaRequest request) throws IOException {
		return null;
	}

	public void handleRequest(InputStream inputStream, OutputStream outputStream, Context context) throws IOException {
		if (StringUtils.isEmpty(customerId) || StringUtils.isEmpty(srcCustomerId)) {
			logger.warn("define customer id and source customer id to mock data");
			after();
			return;
		}
		logger.info("Mocking device content for " + customerId);
		TransactionUtil.updateCustomerId(customerId);
		List<Device> mock = deviceComponent.getDevices(customerId).stream()
				.filter(d -> !d.isVirtual())
				.toList();
		List<Device> src = deviceComponent.getDevices(srcCustomerId).stream()
				.filter(d -> !d.isVirtual())
				.toList();
		for (int ai = 0; ai < mock.size(); ai++) {
			Device device = mock.get(ai);
			Device srcDevice = src.get(ai % mock.size());
			logger.info("adding " + device.getName() + " with " + srcDevice.getName());

			try {
				logger.info("Fetching device data " + device.getName());
				DeviceData srcDeviceData = OSComponent.getLastDeviceEntry(
						srcDevice.getName(), OpenSearchQueries.getDeviceIdQuery(srcDevice.getId()));
				if (srcDeviceData != null) {
					generationComponent.handleDeviceBody(
							TestUtils.getDeviceXML(
									device.getDeviceName(),
									TimeUtils.get15mRoundedDate(),
									srcDeviceData.getAverageCurrent(),
									srcDeviceData.getAverageVoltage(),
									srcDeviceData.getPowerFactor(),
									srcDeviceData.getTotalEnergyConsumed(),
									srcDeviceData.getTotalRealPower()),
							customerId);
				}
			} catch (XPathExpressionException e) {
				logger.warn("error getting device xml ", e);
			}
		}
		after();
	}
}
